import java.io.File;

import org.apache.commons.io.FileUtils;


public class ToString {

	public static void main(String[] args) throws Exception {
		final File f = new File("D:\\Users\\Matt\\Desktop\\HFC WeakAuras\\Archimonde\\Init.lua");

		String s = FileUtils.readFileToString(f);

		s = s.replaceAll("\"", "\\\\\"");

		String out = "local s = [==[";
		String encoded = encode(s);
		String decoded = decode(encoded);
		out += encoded;
		out += "]==]";

		System.out.println(out);
	}

	static String encode(String s) {
		String out = "";
		for (String line : s.split("\n")) {
			if (!line.trim().isEmpty()) {
				line = line.trim();
				for (char c : line.toCharArray()) {
					c -= 2;
					out += c;
				}
				out += "@";
			}
		}
		return out;
	}

	static String decode(String s) {
		String out = "";
		for (String line : s.split("@")) {
			if (!line.trim().isEmpty()) {
				line = line.trim();
				for (char c : line.toCharArray()) {
					c += 2;
					out += c;
				}
				out += "\n";
			}
		}
		return out;
	}

}
