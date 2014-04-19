package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * 书写一个录音类
 * 
 * @author lw
 * 
 */
public class AudioRecord {

	private MainActivity activity;

	private String filePath;

	private MediaRecorder mRecorder;

	private MediaPlayer mPlayer;

	public AudioRecord(MainActivity activity) {
		this.activity = activity;
		try {
			initData();
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @throws FileNotFoundException
	 */
	private void initData() throws FileNotFoundException {
		boolean result = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (result) {
			// sdcard存在的情况下
			filePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			filePath += "/mRecord.amr";
		} else {
			// sdcard不存在的情况下
			activity.openFileOutput("mRecord.amr", Context.MODE_WORLD_WRITEABLE
					| Context.MODE_WORLD_READABLE);
			File file = activity.getFilesDir();
			file = new File(file, "mRecord.amr");
			filePath = file.getAbsolutePath();
		}
	}

	/**
	 * 开始录音
	 */
	public void startRecording() {
		new Thread() {
			@Override
			public void run() {
				mRecorder = new MediaRecorder();
				mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
				mRecorder.setOutputFile(filePath);
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				try {
					mRecorder.prepare();
				} catch (IOException e) {
				}
				mRecorder.start();
				try {
					Thread.sleep(1000 * 30);
				} catch (InterruptedException e1) {
				}
				byte[] buffer = stopRecording();
				handler.sendEmptyMessage(0);
				// /** 上传录音文件 **/
			}

		}.start();

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				Toast.makeText(activity, "录音完成，时间为30秒！", Toast.LENGTH_LONG)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * 停止录音
	 */
	public byte[] stopRecording() {
		// 创建1M的录音空间
		ByteArrayBuffer buffer = new ByteArrayBuffer(1024 * 1024);
		// mRecorder对象释放
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		File file = new File(filePath);
		// 录音数据读取
		try {
			FileInputStream input = new FileInputStream(file);
			int result;
			while ((result = input.read()) != -1) {
				buffer.append(result);
			}
			input.close();
			// 删除文件
			// file.delete();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return buffer.toByteArray();
	}

	public void playing() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(filePath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
		}
	}

	public void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
		new File(filePath).delete();
	}
}
