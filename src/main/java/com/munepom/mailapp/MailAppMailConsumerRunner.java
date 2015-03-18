package com.munepom.mailapp;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.util.ObjectCopyUtil;


public class MailAppMailConsumerRunner extends AbstractMailAppRunner implements ObjectCopyUtil {

	protected Logger log = LoggerFactory.getLogger( this.getClass() );

	private MailConsumer consumer;

	private int consumerThreadNum;

	/**
	 * メール読み取り用スレッド制御
	 */
	private ExecutorService consumerService;

	public MailAppMailConsumerRunner(){
		super();
	}

	public MailAppMailConsumerRunner(String threadName, boolean daemon, BlockingQueue<Path> queue, MailServerProps propsSet, Address[] errorFrom, boolean isParallel) {
		super(threadName, daemon, queue, propsSet, errorFrom, isParallel);
	}

	public MailAppMailConsumerRunner(String threadName, boolean daemon, BlockingQueue<Path> queue, MailServerProps propsSet, Address[] errorFrom, boolean isParallel, MailConsumer consumer, int consumerThreadNum) {
		super(threadName, daemon, queue, propsSet, errorFrom, isParallel);
		this.consumer = consumer;
		this.consumerThreadNum = consumerThreadNum;

		consumerService = Executors.newFixedThreadPool( ! isParallel ? 1 : this.consumerThreadNum,  r -> {
			// daemon 化しておくと、メインスレッド終了後にシャットダウンしてくれる
			Thread t = new Thread(r, threadName);
			t.setDaemon(false);
			return t;
		});
	}

	/**
	 * Queue からメールパスを取得し、処理します
	 */
	@Override
	public void run() {
		try {
			boolean isStopped = false;

			Path mailPath = null;

			// loop forever to watch mail queue
			while (isRunnable) {
				//BlockingQueue を利用すると、来た順に１通ずつ処理可能。
				try {
					mailPath = queue.take();

					if( isReceivedQueueStopper(mailPath) ) {
						log.info("Recieved QueueStopper");
						isStopped = true;
						break;
					}
					else {
						execute(mailPath);
					}
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}

			if( ! isStopped ) {
				log.info("stopping Consumer...");

				// 最終処理
				while(true) {
					try {
						mailPath = queue.take();
						if( isReceivedQueueStopper(mailPath) ) {
							log.info("Recieved QueueStopper");
							break;
						}
						else {
							execute(mailPath);
						}
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
				}
			}

			log.info("Consumer stopped!");
		}
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		finally {
			//shutdown をお忘れなく。すぐに停止するわけではなく、現在のタスクが完了後にシャットダウンしてくれる。
			consumerService.shutdown();
		}
	}

	/**
	 * Queue 停止用オブジェクトを受け取ったら、true
	 * @param mailPath
	 * @return
	 */
	public boolean isReceivedQueueStopper(Path mailPath) {
		return Objects.nonNull(mailPath) && mailPath instanceof QueueStopper;
	}

	public void execute(Path mailPath) {
		MailConsumer clonedConsumer =  (MailConsumer) shallowCopy(consumer);
		clonedConsumer.setMailPath(mailPath);

		consumerService.execute(clonedConsumer);
	}

	@Override
	public void newInstanceCreated() {
		log.info("Start Runner {}", this.threadName);
		ExecutorService service = Executors.newFixedThreadPool(1, r -> {
			Thread t = new Thread(r, this.threadName);
			t.setDaemon(true);
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
	public void stop() {
		log.info("Stop Runner {}", this.threadName);
		isRunnable = false;
	}
}
