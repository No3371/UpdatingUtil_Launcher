package Main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

class loader{
	
}

public class Main {
	static String VariablesURL = "https://dl.dropboxusercontent.com/u/132679455/OnlineServer/Variables.txt";

	public static void main(String[] args) throws IOException, Exception {
		
		loader m = new loader();
	    JFrame frame = new JFrame("");

	    ImageIcon loading = new ImageIcon(m.getClass().getResource("Res/ajax-loader.gif"));
	    frame.add(new JLabel("載入中... ", loading, JLabel.CENTER));

	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(160, 45);
	    frame.setLocationRelativeTo(null);
	    frame.setUndecorated(true);
	    frame.setVisible(true);
		
		// Get online version, if there's newer version, proceed.
		while (checkConnection() == 0) {
			int r = JOptionPane.showConfirmDialog(null, "無法連線到Dropbox，要重試嗎？",
					"發生問題", JOptionPane.YES_NO_OPTION);
			if (r == 1) {
				System.exit(1);
			} else if (r == 0) {
				continue;
			}
		}
		
		String FN = getVariables("MCLauncherFN"), url = getVariables("MCLauncherURL");
		download(FN, new URL(url));
		
		File core = new File(FN);
		while(!core.exists()) Thread.sleep(500);
		core.deleteOnExit();
		ProcessBuilder builder = new ProcessBuilder("java", "-jar", FN);
		Process process = builder.start();
		frame.setVisible(false);
		process.waitFor();
		System.exit(1);

	}

	public static String getVariables(String wanted) throws IOException {
		URL url = new URL(VariablesURL);
		BufferedReader bf = new BufferedReader(new InputStreamReader(
				url.openStream()));
		String str, Name, Value = "";

		while ((str = bf.readLine()) != null) {
			int[] index = { str.indexOf("\"") + 1, 0 };
			index[1] = str.indexOf("\"", index[0] + 1);
			Name = str.substring(index[0], index[1]);
			
			if (!Name.equals(wanted)) {
				continue;
			}

			index[0] = str.indexOf("\"", index[1] + 1) + 1;
			index[1] = str.indexOf("\"", index[0] + 1);
			Value = str.substring(index[0], index[1]);
			bf.close();
			return Value;

		}
		bf.close();
		return Value;

	}

	public static void download(String fn, URL url) throws IOException {
		if (fn.contains("\\")) {
			new File(fn).getParentFile().mkdirs();
		}
		InputStream in;
		try {
			in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();

			FileOutputStream fos;
			fos = new FileOutputStream(fn);
			fos.write(response);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
	}

	public static int checkConnection() throws IOException,
			InterruptedException {
		Process p1 = java.lang.Runtime.getRuntime().exec(
				"ping -n 1 www.dropbox.com");
		int returnVal = p1.waitFor();
		boolean reachable = (returnVal == 0);
		if (reachable == false) {
			Thread.sleep(5000);
			return 0;

		} else if (reachable == true) {
			return 1;
		} else {
			Thread.sleep(5000);
			return 0;
		}
	}
}
