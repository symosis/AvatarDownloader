package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) {

		// http://watchseries.ag/episode/avatar:_the_last_airbender_s1_e1.html
		
		//next: the video
		
		String season = "1";
		String from = "2";
		String to = "2";
		String watchseries = "avatar:_the_last_airbender_";
		
//		String watchseries = "the_legend_of_korra_";
		
		args = new String[] { season, from, to, watchseries };
		for (int i = Integer.parseInt(args[1]); i <= Integer.parseInt(args[2]); i++) {
			threaded(Integer.parseInt(args[0]), i, args[3]);
		}

		System.out.println("Done");
	}

	private static void threaded(final int season, final int episode, final String name) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Starting.... " + season + " " + episode);
				try {
					Thread.sleep(100 * episode);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("GO!.... " + season + " " + episode);
				Loader loader = new Loader(season, episode, name);
				int result = loader.start();
							
				if (result != 0)
				{
					System.err.println("error ripping: " + loader.spec + " code: " + result);
					return;
				}

				System.out.println("Done... converting...");
				try {
					ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-i", loader.filename, "-c", "copy",
							"-copyts", loader.filename + ".mp4");
					pb.directory(new File("./download"));
					pb.redirectErrorStream(true);
					Process process = pb.start();

					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = null;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		});
		t.start();
	}

}
