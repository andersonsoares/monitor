package utils;

import models.EventAnalysis;
import play.Logger;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import controllers.routes;

/**
 * Class that will process email and send 
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */
public class SendMail {

	
	public static void sendNotifyEventFinishedTo(String email, EventAnalysis eventAnalysis) {
		Logger.info("Notifying '"+email+"'that his analysis has finished!");
		try {
			MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
			mail.setSubject("Your analysis is ready");
			mail.addRecipient(email);
			mail.addFrom("Twitter Monitor <twittermonitorr@gmail.com>");
			//sends html
			mail.sendHtml("<html><body>" +
					"You can see your analysis at: http://twittermonitor.andersonsoares.info" +
					routes.EventController.pageAnalysisDetails(eventAnalysis.getEvent().getId().toString(), eventAnalysis.getId().toString(), "all", 1, 5) +
					"</body></html>" );
		} catch(Exception e) {
			Logger.error("Could not send email");
			Logger.error(e.getMessage());
		}
		
	}
	
	public static void send(Exception e) {
		System.out.println("Exception ocorreu. Enviando email(Not implemented yet)");
		e.printStackTrace();
	}
	
}
