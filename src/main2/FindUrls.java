package main2;

import java.util.*;
import java.util.regex.*;

public class FindUrls
{
	public static List<String> extractUrls(String input)
	{
		List<String> result = new ArrayList<String>();

		Pattern pattern = Pattern.compile(
				"\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

		Matcher matcher = pattern.matcher(input);
		while (matcher.find())
		{
			result.add(matcher.group());
		}

		return result;
	}

	public static List<String> extractMp4Urls(String input)
	{
		List<String> result = new ArrayList<String>();

		Pattern pattern = Pattern.compile(
				"\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\.mp4");

		Matcher matcher = pattern.matcher(input);
		while (matcher.find())
		{
			result.add(matcher.group());
		}

		return result;
	}

}