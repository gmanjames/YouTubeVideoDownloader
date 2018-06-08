package co.cubbard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.api.client.util.Lists;

import co.cubbard.util.CubbardFileHandle;
import co.cubbard.video.Download;
import co.cubbard.video.PickVideoDownload;
import co.cubbard.video.Title;
import co.cubbard.video.VideoSearch;
import co.cubbard.video.YouTubeSearch;

public class App
{
	private static final Properties properties = new Properties();
	
	static {
		loadProperties(properties);
	}
	
	
	private static void loadProperties(Properties props) {
		try {
			InputStream in = YouTubeSearch.class.getResourceAsStream("/app.properties");
			properties.load(in);
		} catch (IOException e) {
			throw new RuntimeException("Unable to read properties file!");
		}
	}
	
	private static Thread spawnDownloadThread(Title title) {
		return new Thread() {
			@Override
			public void run() {
				download(new PickVideoDownload(properties.getProperty("pickvideo.url.base")), title);
			}
		};
	}
	
	private static List<Title> getTitles(VideoSearch vs, String keyword) {
		return vs.search(keyword);
	}
	
	private static void download(Download strategy, Title title) {
		File targetDir = new File(properties.getProperty("targetPath") + "/" + title.getKeyword());
		if (Files.notExists(targetDir.toPath())) {
			targetDir.mkdir();
		}
		strategy.download(title, new File(targetDir, title.getTitle() + ".mp4"));
	}
	
	private static boolean isComplete(List<Thread> threads) {
		for (Thread thread : threads) {
			if (thread.isAlive()) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String...args) throws IOException {
		VideoSearch search = new YouTubeSearch(
			properties.getProperty("youtube.apiKey"),
			properties.getProperty("youtube.url.base")
		);

		// Testing with threads
		System.out.println("Begin downloading videos...");
		List<Thread> threads = Lists.newArrayList();
		for (String phrase : CubbardFileHandle.getKeywords()) {
			List<Title> titles = getTitles(search, phrase);
			for (Title title : titles) {
				Thread t = spawnDownloadThread(title);
				t.start();
				threads.add(t);
			}
		}
		
		while (!isComplete(threads)) {
			
		}
		
		System.out.println("");
		System.out.println("End.");
	}
}
