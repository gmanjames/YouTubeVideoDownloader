package co.cubbard.video;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PickVideoDownload implements Download
{
	private String pickVideoBaseUrl;
	
	
	public PickVideoDownload(String pickVideoBaseUrl) {
		this.pickVideoBaseUrl = pickVideoBaseUrl;
	}
	
	@Override
	public void download(Title title, File target) {
		// Try with jsoup first...
		String videoDownloadUrl = null;
		try {
			System.out.println(pickVideoBaseUrl + "?video=" + title.getUrl());
			Document doc = Jsoup.connect(pickVideoBaseUrl + "?video=" + title.getUrl()).get();
			
			if (null != doc) {
				Element a = doc.select("table.downloadsTable")
								   .select("a[download='" + title.getTitle() + ".mp4']").first();
				videoDownloadUrl = a.attr("href");
			}
		}
		catch (IOException e) {
			System.out.println("Error connecting to pickvideo");
		}
		
		// Try to download a video
		try {
			if (null != videoDownloadUrl) {
				URL url = new URL(videoDownloadUrl);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				
				// Read requested file input stream
				InputStream in = con.getInputStream();
				
				System.out.println("Begin writing file...");
				java.nio.file.Files.copy(
					in,
					target.toPath(),
					StandardCopyOption.REPLACE_EXISTING
				);
				System.out.println("End...");
				
				IOUtils.closeQuietly(in);
			}
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL!");
		}
		catch (IOException ioe) {
			System.out.println("IO! " + ioe.getCause());
		}
	}
}
