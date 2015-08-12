import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class Obfuscator {

	private static Pattern simpleStringPattern = Pattern.compile("(\".*?\")");
	private static Pattern variablePattern = Pattern.compile("WA_.*?(?=[^a-zA-Z])");

	public static void main(String[] args) {

		final File parentDir = new File("D:\\Users\\Matt\\Desktop\\HFC WeakAuras\\Archimonde");
		//final File parentDir = new File("lib");

		final File f = new File(parentDir, "Init.lua");
		final File f2 = new File(parentDir, "Trigger1.lua");
		final File f3 = new File(parentDir, "Trigger2.lua");
		final File f4 = new File(parentDir, "Untrigger.lua");

		String line = "\"Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Sounds\\sonar.ogg\"".replaceAll("\\\\", "\\\\\\\\");
		Matcher m = simpleStringPattern.matcher(line);
		//System.out.println(m.find());
		//System.out.println(m.group());

		final String s1 = "WA_testing_trigger";

		try {
			obfuscate2(f, f2, f3, f4);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void obfuscate2(final File... f) throws IOException {

		final Set<String> variables = new HashSet<String>();

		for (int i = 0; i < f.length; i++) {
			final File file = f[i];
			final File toUse = FileUtils.getFile(file.getParentFile(), "ob", file.getName());
			FileUtils.copyFile(file, toUse);
			f[i] = toUse;

			String s = FileUtils.readFileToString(toUse);
			for (String line : s.split("\n")) {
				final Matcher m = variablePattern.matcher(line);
				while (m.find()) {
					final String match = m.group();
					variables.add(match);
				}
			}
		}

		final Map<String, String> uuidMap = new HashMap<String ,String>();
		for (final String var : variables) {
			uuidMap.put(var, "v" + String.valueOf(UUID.randomUUID()).replaceAll("\\-", "_"));
		}

		for (int i = 0; i < f.length; i++) {
			final File file = f[i];
			final File toUse = FileUtils.getFile(file.getParentFile(), "ob", file.getName());
			FileUtils.copyFile(file, toUse);
			f[i] = toUse;

			String s = FileUtils.readFileToString(toUse);
			for (final String var : variables) {
				s = s.replaceAll(var, uuidMap.get(var));
			}
			System.out.println(file.getName());
			System.out.println(Squisher.squish(s));
		}

	}

	private static void obfuscate(final File... f) throws IOException {

		final Set<String> variables = new HashSet<String>();

		for (int i = 0; i < f.length; i++) {
			final File file = f[i];
			final File toUse = FileUtils.getFile(file.getParentFile(), "ob", file.getName());
			FileUtils.copyFile(file, toUse);
			f[i] = toUse;

			String s = FileUtils.readFileToString(toUse);

			final Pattern variable = Pattern.compile("WA_.*?(?=[\\s\\(\\[:\\.])");
			for (String line : s.split("\n")) {
				line = line.replaceAll("\\\\", "\\\\");
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
						//System.out.println(line);
						//System.out.print("Replacing:\n\t" +  match + "\nwith\n\t" + replaced + "\n");
						line = line.replaceAll(match, replaced.replaceAll("92, 92", "92"));
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
			FileUtils.writeStringToFile(toUse, s);
		}

		for (final File file : f) {
			String contents = FileUtils.readFileToString(file);
			int count = 1;
			char v = 'a';

			for (final String var : variables) {
				String vToReplace = "";
				for (int j = 0; j < count; j++) {
					vToReplace += v;
				}
				System.out.println(vToReplace + " = " + var);
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
