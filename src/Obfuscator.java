import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class Obfuscator {

	private static Pattern simpleStringPattern = Pattern.compile("(\".*?\")");
	private static Pattern variablePattern = Pattern.compile("WA_.*?(?=[^_a-zA-Z])");

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

		final String s1 = "\"PLAYER_ENTERING_WORLD\"";
		//System.out.println(s1.replaceAll("\"PLAYER_ENTERING_WORLD\"", "replaced"));

		//		final Pattern variable = Pattern.compile("WA_.*?(?=[\\s\\(\\[:\\.])");
		//		Matcher mm = variable.matcher("WA_circle:SetPoint(\"CENTER\", WA_radar_frame, \"CENTER\")");
		//		while (mm.find()) {
		//			System.out.println(mm.group());
		//		}

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


				Matcher m = simpleStringPattern.matcher(line);
				while (m.find()) {
					String match = m.group();
					String replace = "WA_decodeString(";
					boolean comma = false;
					for (char c : match.toCharArray()) {
						if (c == '"') {
							continue;
						}
						if (comma) {
							replace += ", ";
						}
						replace += (int) c;
						comma = true;
					}
					replace += ")";
					replace = replace.replaceAll("92, 92", "92");
					match = match.replaceAll("\\\\\\\\", "\\\\\\\\\\\\\\\\");
					//System.out.println("Replacing\n\t" + match + "\nwith\n\t" + replace);
					line = line.replaceAll(match, replace);
					s = s.replaceAll(match, replace);
				}


				m = variablePattern.matcher(line);
				while (m.find()) {
					final String match = m.group();
					variables.add(match);
				}
			}
			FileUtils.writeStringToFile(toUse, s);
		}

		String currentVar = "a";
		final Map<String, String> varMap = new HashMap<String, String>();
		for (final String var : variables) {
			varMap.put(var, "ob_" + currentVar);
			currentVar = increment(currentVar);
		}

		for (int i = 0; i < f.length; i++) {
			final File file = f[i];
			final File toUse = FileUtils.getFile(file.getParentFile(), "ob", file.getName());
			FileUtils.copyFile(file, toUse);
			f[i] = toUse;

			String s = FileUtils.readFileToString(toUse);
			for (final String var : variables) {
				s = s.replaceAll(var, varMap.get(var));
			}
			System.out.println(file.getName());
			System.out.println(Squisher.squish(s));
		}

		for (final String var : variables) {
			System.out.println(varMap.get(var) + " = " + var);
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

	private static String increment(String var) {

		List<String> tokens = Arrays.asList(var.split("|"));

		boolean allZ = true;
		for (int i = tokens.size() - 1; i >= 0; i--) {
			char c = tokens.get(i).charAt(0);
			if (c < 'z') {
				c++;
				tokens.set(i, ""+c);
				allZ = false;
				break;
			}
		}
		if (allZ) {
			var = "";
			for (int i = 0; i < tokens.size(); i++) {
				var += "a";
			}
			var += "a";
		}
		else {
			var = "";
			for (int i = 0; i < tokens.size(); i++) {
				var += tokens.get(i);
			}
		}

		return var;
	}


}
