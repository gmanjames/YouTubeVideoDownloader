package co.cubbard.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.ImmutableList;

import co.cubbard.video.Title;

public final class CubbardFileHandle {
	private static final File keywordFile = new File(System.getProperty("user.dir") + "/data", "keywords.txt");

	private CubbardFileHandle() {
		throw new AssertionError();
	}
	
	public static List<String> getKeywords() throws IOException {
		InputStream inputStream = new FileInputStream(keywordFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		ImmutableList.Builder<String> builder = ImmutableList.<String>builder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.add(line);
		}
		reader.close();
		return builder.build();
	}
}
