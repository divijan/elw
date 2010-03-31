package elw.dp.ui;

import elw.dp.app.Controller;
import elw.vo.Version;
import org.akraievoy.gear.G;
import org.apache.log4j.BasicConfigurator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Applet extends JApplet{
	private static final Logger log = LoggerFactory.getLogger(Applet.class);

	protected Controller instance;

	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}

		BasicConfigurator.configure();

		final ObjectMapper mapper = new ObjectMapper();

		Version ver = null;
		final String verStr = getParameter("ver");
		try {
			ver = mapper.readValue(verStr, Version.class);
		} catch (IOException e) {
			System.err.println(G.report(e));
		}

		final Controller controller = new Controller();
		controller.setSelectedTask(ver);
		controller.setUploadUrl(getCodeBase().toString() + "upload");
		controller.setUploadHeader(getParameter("upHeader"));
		controller.setUploadPath(getParameter("upPath"));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				controller.init();
				instance = controller;
			}
		});
	}

	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (instance != null) {
					getContentPane().setLayout(new BorderLayout());
					getContentPane().add(instance.getView().getRootPanel(), BorderLayout.CENTER);
				}
			}
		});
	}
}
