import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class Obfuscator {

	private static Pattern simpleStringPattern = Pattern.compile("(\".*?\")");

	public static void main(String[] args) throws Exception {

		final File f = new File("lib/Init.lua");
		final File f2 = new File("lib/Trigger1.lua");
		final File f3 = new File("lib/Trigger2.lua");
		final File f4 = new File("lib/Untrigger.lua");

		String s1 = "[\"WARRIOR\"] = { 0, 0.125, 0, 0.25 },";
		//System.out.println(s1.replaceAll("\"WARRIOR\"", "REPLACED"));

		obfuscate(f, f2, f3, f4);

	}

	private static void obfuscate(final File... f) throws IOException {


		final Set<String> variables = new HashSet<String>();

		for (final File file : f) {
			String s = FileUtils.readFileToString(file);

			final Pattern variable = Pattern.compile("WA_.*?[\\s\\(\\[:\\.]");
			for (String line : s.split("\n")) {
				String before = new String(line);
				final Matcher stringMatcher = simpleStringPattern.matcher(line);
				boolean matched = false;
				String str = "";
				String match;
				while (stringMatcher.find()) {
					match = stringMatcher.group();
					match = match.replaceAll("[\"]", "");
					if (match.length() > 0) {
						match = "\"" + match + "\"";
						//System.out.println(match);

						String replaced = "WA_stringDecode(";
						boolean comma = false;
						for (final char c : match.toCharArray()) {
							if (c == '"') {
								continue;
							}
							if (comma) {
								replaced += ", ";
							}
							replaced += (int) c;
							comma = true;
						}
						replaced += ")";

						//System.out.println(replaced);
						System.out.println(line);
						System.out.print("Replacing:\n\t" +  match + "\nwith\n\t" + replaced + "\n");
						line = line.replaceAll(match, replaced);
					}
					matched = true;
				}
				final Matcher m = variable.matcher(line);
				matched = false;
				while (m.find()) {
					matched = true;
					match = m.group();
					match = match.substring(0, match.length() - 1).trim();
					if (match.length() > 0) {
						variables.add(match.replaceAll("[^a-zA-Z_]", ""));
					}
				}
				s = s.replace(before, line);
			}
			FileUtils.writeStringToFile(file, s);
		}

		for (final File file : f) {
			String contents = FileUtils.readFileToString(file);
			int count = 1;
			char v = 'a';

			for (final String var : variables) {
				String vToReplace = "";
				for (int i = 0; i < count; i++) {
					vToReplace += v;
				}
				//System.out.println(vToReplace + " = " + var);
				contents = contents.replaceAll(var, vToReplace);
				v++;
				if (v > 'z') {
					v = 'a';
					count++;
				}
			}
			System.out.println(file.getName());
			final String squished = Squisher.squish(contents);
			System.out.println(squished);
			FileUtils.writeStringToFile(new File(file.getParentFile(), file.getName().replace(".lua", "") + "_OB.lua"), contents);
			System.out.println();
		}
	}


}
