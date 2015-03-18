package com.munepom.mailapp;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * メール読み出し実行
 * @author nishimura
 *
 */
public class MailAppMailServerRunner extends AbstractMailAppRunner {

	protected Logger log = LoggerFactory.getLogger( this.getClass() );

	private MailReader reader;

	/**
	 * メール読み取り間隔 (ミリ秒)
	 */
	private long span = 30000L;

	public MailAppMailServerRunner(){
		super();
	}

	public MailAppMailServerRunner(String threadName, boolean daemon, BlockingQueue<Path> queue, MailServerProps propsSet, Address[] errorFrom, boolean isParallel, MailReader reader, long span) {
		super(threadName, daemon, queue, propsSet, errorFrom, isParallel);
		this.reader = reader;
		this.span = span;
	}

	/**
	 * メールを読み、コピー後、処理スレッドにリストを渡します。
	 */
	@Override
	public void run() {
		Store store = null;	// AutoClosable が使えない
		try {
			Properties mailProps = propsSet.getMailProps();
			String host = propsSet.getHost();
			String user = propsSet.getUser();
			String password = propsSet.getPassword();
			String mbox = propsSet.getMbox();
			String protocol = propsSet.getProtocol();
			Address[] errorFrom = this.errorFrom;

			Session session = Session.getInstance(mailProps, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});

			store = session.getStore(protocol);

			Folder folder = null;
			// loop forever to watch mail server
			while (isRunnable) {
				log.info("reading...");

				try {
					store.connect(host, user, password);

					folder = store.getFolder(mbox);	// for test
//					folder = store.getFolder("INBOX");	//read all messages
					int cntMsg = folder.getMessageCount();
					log.debug("folder: " + folder.getName());
					log.debug("cntMsg: " + cntMsg);

					List<Path> mailPaths = reader.apply(folder);
					if( Objects.nonNull(mailPaths) ) {
						Stream<Path> stream = null;
						if( isParallel ) {
							stream = mailPaths.parallelStream();
						}
						else {
							stream = mailPaths.stream();
						}
						stream.forEach(path -> queue.add(path));
					}
//					// open the folder
//					folder.open(Folder.READ_WRITE);
//
//					// メール読み出し & コピー & キュー渡し
//					// folder.close() の前に リストをコピーしておかないと、javax.mail.FolderClosedException が発生する！
//					// com.sun.mail.imap.IMAPMessage が Serializable を実装していないので、オブジェクトコピーは無理。
//					// 一旦ファイルに書き出し、ファイル名を Queue に渡して処理させる。
//					Stream<Message> stream = null;
//					if( isParallel ) {
//						stream = Arrays.stream( Optional.ofNullable(folder.getMessages()).orElse(new Message[]{}) ).parallel();
//					}
//					else {
//						stream = Arrays.stream( Optional.ofNullable(folder.getMessages()).orElse(new Message[]{}) );
//					}
//
//					stream
//					.filter(message -> {
//						try {
//							return ! message.isSet(Flags.Flag.DELETED);
//						} catch (Exception e) {
//							log.error(e.getMessage(), e);
//						}
//						return false;
//					})
//					.map(message -> (MimeMessage) message)
//					.map(mime -> {
//						Path copiedPath = copyEmail(tmpPath, session, mime);
//						if( Objects.isNull(copiedPath) ) {
//							copyEmail(failedPath, session, mime);  //メールコピー失敗時は、失敗パスへコピー。
//						}
//						return new DataSetMessageCopiedPath(mime, copiedPath);
//					})
//					.filter(dataSet -> Objects.nonNull(dataSet.getCopiedPath()))
//					.forEach(dataSet -> {
//						try {
//							queue.add(dataSet.getCopiedPath());  // 保存できたら、ファイル名を Queue へ渡す
//							dataSet.getMessage().setFlag(Flags.Flag.DELETED, true);  //保存できたら、メール削除
//						} catch (Exception e) {
//							sendErrorMail(session, new DataSetMime());
//							log.error(e.getMessage(), e);
//						}
//					});
				}
				finally {
					if( store != null ) {
						try {
							store.close();
						} catch (MessagingException e) {
							//TODO 管理者にメール
							log.error(e.getMessage(), e);
						}
					}
				}

				log.info("read done!!!");

				if( isRunnable ) {
					Thread.sleep(span);
				}
			}
		}
		catch (InterruptedException | MessagingException e) {
			//TODO 管理者にメール
			log.error(e.getMessage(), e);
		}
		finally {
			//処理終了時、特殊オブジェクトを入れておく。
			log.info("Set QueueStopper");
			queue.add(new QueueStopper());
		}
	}

	@Override
	public void newInstanceCreated() throws Exception {
		log.info("Start Runner {}", this.threadName);
		ExecutorService service = Executors.newFixedThreadPool(1, r -> {
			Thread t = new Thread(r, this.threadName);
			t.setDaemon(false);
			return t;
		});
		try {
			service.execute(this);
		}
		finally {
			service.shutdown();
		}
	}

	@Override
	public void stop() throws Exception {
		log.info("Stop Runner {}", this.threadName);
		isRunnable = false;
	}

}
