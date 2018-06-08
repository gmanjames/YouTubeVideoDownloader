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
		
		// 1. Randomly select two keywords that will consistute the YouTube searches
		List<String> keywords = CubbardFileHandle.getKeywords();
		int randomInt1 = ThreadLocalRandom.current().nextInt(0, keywords.size());
		int randomInt2 = ThreadLocalRandom.current().nextInt(0, keywords.size());
		
		while (randomInt1 == randomInt2) {
			randomInt2 = ThreadLocalRandom.current().nextInt(0, keywords.size());
		}

		String keyword1 = keywords.get(randomInt1);
		String keyword2 = keywords.get(randomInt2);
		System.out.println(String.format("selected '%s' and '%s'", keyword1, keyword2));
		
		List<Title> titles1 = getTitles(search, keyword1);
		List<Title> titles2 = getTitles(search, keyword2);
		
		// Testing with threads
		System.out.println("Begin downloading videos...");

		List<Thread> threads = Lists.newArrayList();
		for (Title title : titles1) {
			Thread t = spawnDownloadThread(title);
			t.start();
			threads.add(t);
		}
		
		for (Title title : titles2) {
			Thread t = spawnDownloadThread(title);
			t.start();
			threads.add(t);
		}
		
		int lineCount = 0;
		while (!isComplete(threads)) {
			lineCount++;
			try {
				if ((lineCount % 32) == 0)
					System.out.println(".");
				else
					System.out.print(".");
					
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Main thread of execution interupted.");
			}
		}
		
		System.out.println("");
		System.out.println("End.");
	}
}
