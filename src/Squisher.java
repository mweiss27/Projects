import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;


public class Squisher {

	private static Pattern function = Pattern.compile("function\\(.*?\\)"); // Matches a variable = function(...) header
	private static Pattern comment = Pattern.compile("\\-\\-\\[\\[.*?$"); //Matches a --[[comment at the end of a line
	private static Pattern noSemi = Pattern.compile("(then|do|else|end|;|\\{|\\}|\\[|\\])"); // Matches for this do NOT need a semicolon after

	public static void main(String[] args) throws Exception {
		final File f = new File("D:\\Users\\Matt\\Desktop\\HFC WeakAuras\\Archimonde\\Init.lua");
		final File f2 = new File("D:\\Users\\Matt\\Desktop\\HFC WeakAuras\\Archimonde\\Trigger2.lua");


		System.out.println("Init");
		System.out.println(squish(f));

		System.out.println("\nTrigger2");
		System.out.println(squish(f2));
	}

	private static String squish(final File f) throws IOException {
		final String s = FileUtils.readFileToString(f); //Replace -- with --[[ to block comments
		return squish(s);
	}

	public static String squish(String s) {
		s = s.replaceAll("\\-\\-", "\\-\\-[[");
		String out = "";

		for (String line : s.split("\n")) {
			line = line.trim();
			if (comment.matcher(line).find()) {
				line = line + "--]]";
				line = line.replaceAll("--\\[\\[.*?\\--\\]\\]", ""); //Remove comments
			}
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			if (noSemi.matcher(line).find() || function.matcher(line).find()) {
				out += line + " ";
			}
			else {
				out += line + ";";
			}
		}
		return out;
	}

}
