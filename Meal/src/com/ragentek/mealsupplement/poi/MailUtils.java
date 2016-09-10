package com.ragentek.mealsupplement.poi;

import org.apache.log4j.Logger;

//import com.wheatek.bean.MailServer;
//import com.wheatek.dwr.Result;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by zixiao.zhang on 2015/9/11.
 */
public class MailUtils {
//	private String host = "smtp.qq.com"; // smtp鏈嶅姟鍣�,杩欎釜涓�鑸笉闇�瑕佽嚜宸遍厤缃紝浣跨敤鍏徃鐨勫嵆鍙�傚綋鐒讹紝涔熷彲浠ヤ娇鐢ㄧ涓夋柟濡俼q(smtp.qq.com),126閭,姝ゆ椂鐢ㄦ埛鍚嶅瘑鐮佸氨鏄嚜宸辨敞鍐岀殑閭鍚�
//	private String port = "25"; //smtp鏈嶅姟鍣ㄩ粯璁ょ鍙ｏ紝榛樿涓�25
//	private String user = "1187597904@qq.com"; // 鐢ㄦ埛鍚�
//	private String pwd = "liuyingjing1995"; // 瀵嗙爜
//	private String from = "1187597904@qq.com"; // 鍙戜欢浜哄湴鍧�
//	private List<String> to = new ArrayList<String>(); // 鏀朵欢浜哄湴鍧�
//	private String subject = "abc123"; // 閭欢鏍囬
	
	private String url = ""; // 鍥炲埌鍦板潃锛岄偖浠朵腑浼氱偣鍑昏鍦板潃閲嶆柊璁块棶鏈嶅姟鍣�
	
	
//	private String host = "mail.wheatek.com"; // smtp鏈嶅姟鍣�,杩欎釜涓�鑸笉闇�瑕佽嚜宸遍厤缃紝浣跨敤鍏徃鐨勫嵆鍙�傚綋鐒讹紝涔熷彲浠ヤ娇鐢ㄧ涓夋柟濡俼q(smtp.qq.com),126閭,姝ゆ椂鐢ㄦ埛鍚嶅瘑鐮佸氨鏄嚜宸辨敞鍐岀殑閭鍚�
//	private String port = "25"; //smtp鏈嶅姟鍣ㄩ粯璁ょ鍙ｏ紝榛樿涓�25
////	private String user = "dccsw"; // 鐢ㄦ埛鍚�
//	private String user = "yingjing.liu@Wheatek.com"; // 鐢ㄦ埛鍚�
//	private String pwd = " Wheatek#"; // 瀵嗙爜
//	private String from = "jinzhao.qiao@wheatek.com"; // 鍙戜欢浜哄湴鍧�
//	private List<String> to = new ArrayList<String>(); // 鏀朵欢浜哄湴鍧�
//	private String subject = "abc123"; // 閭欢鏍囬


	private String host = "mail.wheatek.com"; // smtp鏈嶅姟鍣�,杩欎釜涓�鑸笉闇�瑕佽嚜宸遍厤缃紝浣跨敤鍏徃鐨勫嵆鍙�傚綋鐒讹紝涔熷彲浠ヤ娇鐢ㄧ涓夋柟濡俼q(smtp.qq.com),126閭,姝ゆ椂鐢ㄦ埛鍚嶅瘑鐮佸氨鏄嚜宸辨敞鍐岀殑閭鍚�
	private String port = "25"; //smtp鏈嶅姟鍣ㄩ粯璁ょ鍙ｏ紝榛樿涓�25
	//	private String user = "dccsw"; // 鐢ㄦ埛鍚�
	private String user = "dccsw"; // 鐢ㄦ埛鍚�
	private String pwd = "Wheatek!@"; // 瀵嗙爜
	private String from = "jinzhao.qiao@wheatek.com"; // 鍙戜欢浜哄湴鍧�
	private List<String> to = new ArrayList<String>(); // 鏀朵欢浜哄湴鍧�
	private String subject = "abc123"; // 閭欢鏍囬
	
	private String title;
	private String attach;
	private static Logger logger = Logger.getLogger(MailUtils.class);
	Pattern p = Pattern.compile(
			"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

//	public MailUtils() {
//		this((String)null);
//	}
	
	/*public MailUtils(String customerName) {
//		ConfigUtil configUtil = new ConfigUtil(customerName);
//		host = configUtil.getProperty("Mail_host");
//		port = configUtil.getProperty("Mail_port");
//		user = configUtil.getProperty("Mail_user");
//		pwd = configUtil.getProperty("Mail_pwd");
//		from = configUtil.getProperty("Mail_from");
//		title = configUtil.getProperty("Mail_title");
//		attach = configUtil.getProperty("Mail_signatures");
		MailServer bean = ConfigUtil.getMailServer(customerName);
		host = bean.getHost();
		port = String.valueOf(bean.getPort());
		user = bean.getUserName();
		pwd = bean.getPassword();
		from = bean.getFromEmail();
		title = bean.getTitle();
		attach = bean.getSignatures();
		//logger.info(host + ":" + user + ":" + pwd + ":" + from + ":" + url + ":" + title);
		//logger.info(attach);
	}
	
	public MailUtils(MailServer bean) {
		host = bean.getHost();
		port = String.valueOf(bean.getPort());
		user = bean.getUserName();
		pwd = bean.getPassword();
		from = bean.getFromEmail();
		title = bean.getTitle();
		attach = bean.getSignatures();
	}
*/
	public void setAddress(String subject, String to) {
		logger.info("TO:" + to);
		logger.info("Subject:" + subject);
		if (to != null && !to.equals("")) {
			String[] tos = to.split(";");
			for (int i = 0; i < tos.length; i++) {
				// modify by kui.li ,Remove the spaces around of email string
				// ,20150306, start
				tos[i] = tos[i].trim();
				// modify by kui.li ,Remove the spaces around of email string
				// ,20150306, end
				if (!tos[i].equals("") && p.matcher(tos[i]).matches()) {
					this.to.add(tos[i]);
				}
			}
		}
		this.subject = subject;
	}

	public boolean send(String txt, String cc, String bcc) {
		boolean res = false; 
		// if(true)return;
		Properties props = new Properties();
		// 璁剧疆鍙戦�侀偖浠剁殑閭欢鏈嶅姟鍣ㄧ殑灞炴�э紙杩欓噷浣跨敤缃戞槗鐨剆mtp鏈嶅姟鍣級
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		// 闇�瑕佺粡杩囨巿鏉冿紝涔熷氨鏄湁鎴峰悕鍜屽瘑鐮佺殑鏍￠獙锛岃繖鏍锋墠鑳介�氳繃楠岃瘉锛堜竴瀹氳鏈夎繖涓�鏉★級
		props.put("mail.smtp.auth", "true");
		// 鐢ㄥ垰鍒氳缃ソ鐨刾rops瀵硅薄鏋勫缓涓�涓猻ession
		Session session = Session.getDefaultInstance(props);
		// 鏈変簡杩欏彞渚垮彲浠ュ湪鍙戦�侀偖浠剁殑杩囩▼涓湪console澶勬樉绀鸿繃绋嬩俊鎭紝渚涜皟璇曚娇
		// 鐢紙浣犲彲浠ュ湪鎺у埗鍙帮紙console)涓婄湅鍒板彂閫侀偖浠剁殑杩囩▼锛�
		session.setDebug(false);
		// 鐢╯ession涓哄弬鏁板畾涔夋秷鎭璞�
		MimeMessage message = new MimeMessage(session);
		try {
			// 鍔犺浇鍙戜欢浜哄湴鍧�
			message.setFrom(new InternetAddress(from));
			// 鍔犺浇鏀朵欢浜哄湴鍧�
			for (int i = 0; i < to.size(); i++) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.get(i)));
			}
			if (!cc.equals("")) {
				String[] ccs = cc.split(";");
				for (int i = 0; i < ccs.length; i++) {
					if (!ccs[i].equals("") && p.matcher(ccs[i]).matches()) {
						message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccs[i]));
					}
				}
			}
			if (!bcc.equals("")) {
				String[] bccs = bcc.split(";");
				for (int i = 0; i < bccs.length; i++) {
					if (!bccs[i].equals("") && p.matcher(bccs[i]).matches()) {
						message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccs[i]));
					}
				}
			}
			// 鍔犺浇鏍囬
			message.setSubject(title + subject);
			// 鍚憁ultipart瀵硅薄涓坊鍔犻偖浠剁殑鍚勪釜閮ㄥ垎鍐呭锛屽寘鎷枃鏈唴瀹瑰拰闄勪欢
			Multipart multipart = new MimeMultipart();

			// 璁剧疆閭欢鐨勬枃鏈唴瀹�
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(txt + attach, "text/html;charset=utf-8");
			multipart.addBodyPart(contentPart);

			logger.info("CC:" + cc);

			logger.info("BCC:" + bcc);

			// 娣诲姞闄勪欢
			// BodyPart messageBodyPart = new MimeBodyPart();
			// DataSource source = new FileDataSource(affix);
			// 娣诲姞闄勪欢鐨勫唴瀹�
			// messageBodyPart.setDataHandler(new DataHandler(source));
			// 娣诲姞闄勪欢鐨勬爣棰�
			// 杩欓噷寰堥噸瑕侊紝閫氳繃涓嬮潰鐨凚ase64缂栫爜鐨勮浆鎹㈠彲浠ヤ繚璇佷綘鐨勪腑鏂囬檮浠舵爣棰樺悕鍦ㄥ彂閫佹椂涓嶄細鍙樻垚涔辩爜
			// sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			// messageBodyPart.setFileName("=?GBK?B?"+
			// enc.encode(affixName.getBytes()) + "?=");
			// multipart.addBodyPart(messageBodyPart);

			// 灏唌ultipart瀵硅薄鏀惧埌message涓�
			message.setContent(multipart);
			// 淇濆瓨閭欢
			message.saveChanges();
			
			// 鍙戦�侀偖浠�
			Transport transport = session.getTransport("smtp");
			//鎵撳嵃瀵嗙爜
			System.out.println("host="+host+", user="+user+", pwd="+pwd);
			// 杩炴帴鏈嶅姟鍣ㄧ殑閭
			transport.connect(host, user, pwd);
			// 鎶婇偖浠跺彂閫佸嚭鍘�
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("mail send success");
			res = true;
		} catch (Exception e) {
			logger.error("mail send error", e);
		}
		return res;
	}
	
	public String sendResult(String txt, String cc, String bcc) {
		String msg = null;
		// if(true)return;
		Properties props = new Properties();
		// 璁剧疆鍙戦�侀偖浠剁殑閭欢鏈嶅姟鍣ㄧ殑灞炴�э紙杩欓噷浣跨敤缃戞槗鐨剆mtp鏈嶅姟鍣級
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		// 闇�瑕佺粡杩囨巿鏉冿紝涔熷氨鏄湁鎴峰悕鍜屽瘑鐮佺殑鏍￠獙锛岃繖鏍锋墠鑳介�氳繃楠岃瘉锛堜竴瀹氳鏈夎繖涓�鏉★級
		props.put("mail.smtp.auth", "true");
		// 鐢ㄥ垰鍒氳缃ソ鐨刾rops瀵硅薄鏋勫缓涓�涓猻ession
		Session session = Session.getDefaultInstance(props);
		// 鏈変簡杩欏彞渚垮彲浠ュ湪鍙戦�侀偖浠剁殑杩囩▼涓湪console澶勬樉绀鸿繃绋嬩俊鎭紝渚涜皟璇曚娇
		// 鐢紙浣犲彲浠ュ湪鎺у埗鍙帮紙console)涓婄湅鍒板彂閫侀偖浠剁殑杩囩▼锛�
		session.setDebug(false);
		// 鐢╯ession涓哄弬鏁板畾涔夋秷鎭璞�
		MimeMessage message = new MimeMessage(session);
		try {
			// 鍔犺浇鍙戜欢浜哄湴鍧�
			message.setFrom(new InternetAddress(from));
			// 鍔犺浇鏀朵欢浜哄湴鍧�
			for (int i = 0; i < to.size(); i++) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.get(i)));
			}
			if (!cc.equals("")) {
				String[] ccs = cc.split(";");
				for (int i = 0; i < ccs.length; i++) {
					if (!ccs[i].equals("") && p.matcher(ccs[i]).matches()) {
						message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccs[i]));
					}
				}
			}
			if (!bcc.equals("")) {
				String[] bccs = bcc.split(";");
				for (int i = 0; i < bccs.length; i++) {
					if (!bccs[i].equals("") && p.matcher(bccs[i]).matches()) {
						message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccs[i]));
					}
				}
			}
			// 鍔犺浇鏍囬
			message.setSubject(title + subject);
			// 鍚憁ultipart瀵硅薄涓坊鍔犻偖浠剁殑鍚勪釜閮ㄥ垎鍐呭锛屽寘鎷枃鏈唴瀹瑰拰闄勪欢
			Multipart multipart = new MimeMultipart();

			// 璁剧疆閭欢鐨勬枃鏈唴瀹�
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(txt + attach, "text/html;charset=utf-8");
			multipart.addBodyPart(contentPart);

			logger.info("CC:" + cc);

			logger.info("BCC:" + bcc);

			// 娣诲姞闄勪欢
			// BodyPart messageBodyPart = new MimeBodyPart();
			// DataSource source = new FileDataSource(affix);
			// 娣诲姞闄勪欢鐨勫唴瀹�
			// messageBodyPart.setDataHandler(new DataHandler(source));
			// 娣诲姞闄勪欢鐨勬爣棰�
			// 杩欓噷寰堥噸瑕侊紝閫氳繃涓嬮潰鐨凚ase64缂栫爜鐨勮浆鎹㈠彲浠ヤ繚璇佷綘鐨勪腑鏂囬檮浠舵爣棰樺悕鍦ㄥ彂閫佹椂涓嶄細鍙樻垚涔辩爜
			// sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			// messageBodyPart.setFileName("=?GBK?B?"+
			// enc.encode(affixName.getBytes()) + "?=");
			// multipart.addBodyPart(messageBodyPart);

			// 灏唌ultipart瀵硅薄鏀惧埌message涓�
			message.setContent(multipart);
			// 淇濆瓨閭欢
			message.saveChanges();
			// 鍙戦�侀偖浠�
			Transport transport = session.getTransport("smtp");
			// 杩炴帴鏈嶅姟鍣ㄧ殑閭
			transport.connect(host, user, pwd);
			// 鎶婇偖浠跺彂閫佸嚭鍘�
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("mail send success");
			msg = "Send Email Successfully!";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			StringBuilder sb = new StringBuilder("Send Email Failure!!!<br/>");
			sb.append(e.toString());
			sb.append("<br/>");
			StackTraceElement[] elems = e.getStackTrace();
			for(StackTraceElement ele : elems) {
				sb.append("at ");
				sb.append(ele.toString());
				sb.append("<br/>");
			}
			msg = sb.toString();
		}
		return msg;
	}

/*	private String getUrl() {
		ConfigUtil defUtil = ConfigUtil.getDefault();
		return defUtil.getProperty("Mail_url");
		//return url;
	}
	*/
	/**
	 * 閫氳繃getServerName鍜実etServerPort鎴栬�呮槸浠巖equestURL涓埅鍙栧彲浠ヨ幏寰楄姹傜殑ip鍜岀鍙ｏ紝鑰屼笖濡傛灉ip鏄煙鍚嶅彲浠ヨ幏寰楀煙鍚嶏紝浣嗘槸杩欐牱濡傛灉璁块棶鏃舵槸localhost鎴栫洿鎺p灏变篃鏄痩ocalhost鎴杋p锛屽湪localhost涓嬪彂鍑洪偖浠讹紝鏀跺埌鍚庤繕鏄痩ocalhost杩欒偗瀹氫笉琛�
	 * 鍦╥p涓嬪彂鍑洪偖浠讹紝涓囦竴閭欢鍙戝嚭鍚庢洿鏀逛簡閮ㄧ讲鏈嶅姟鍣紝涔熸槸鏈夐闄╃殑
	 * 閫氳繃LocalHost鏉ヨ幏寰楃殑棣栧厛鍩熷悕閮借幏涓嶅緱锛屽彧鑳借幏寰梚p锛屽悓鏍锋湁ip鎯呭喌涓嬪彂閭欢鐨勯闄�
	 * 缁煎悎鏉ヨ锛岀洰鍓嶈繕鏃犳硶缁煎悎澶勭悊鍚勭鎯呭喌锛屼笉杩囪�冭檻鍒板悗缁偗瀹氫細浣跨敤鍩熷悕鏉ヨ闂紝鏁呯洰鍓嶉噰鍙栧涓嬬殑绛栫暐
	 * @param req
	 * @return
	 */
	public String getUrl(HttpServletRequest req) {
		String url = null;
		try {
			//logger.info(InetAddress.getLocalHost().getHostName()+":"+InetAddress.getLocalHost().getHostAddress()+":"+InetAddress.getLocalHost().getCanonicalHostName()+":"+req.getRemoteAddr()+":"+req.getLocalAddr()+":"+req.getLocalName()+":"+req.getLocalPort());
			//logger.info(req.getServerName()+":"+req.getServerPort());
			//logger.info(req.getRequestURL()+":"+req.getRequestURI());
			int index = req.getRequestURL().indexOf(req.getRequestURI());
			if(index > 0) {
				url = req.getRequestURL().substring(0, index);
			}
			/*int port = req.getServerPort();
			if(!TextUtil.isNullOrEmpty(req.getServerName())) {
				if(port == 80) {
					url = "http://"+req.getServerName();
				} else {
					url = "http://"+req.getServerName()+":"+req.getServerPort();
				}
			}*/
		} catch(Exception e) {}
//		if(url == null) {
//			url = getUrl();
//		}
		return url;
	}

	public static void main(String[] args) {
		 MailUtils mailUtil = new MailUtils();
		 mailUtil.setAddress("subject", "jinzhao.qiao@wheatek.com"); //闇�瑕佹洿鏀筿q.properties鏂囦欢涓殑qq閭鍚嶇О鍜屽瘑鐮佹墠鑳介獙璇�
		 mailUtil.send("hahha", "abc", "def");
//		try {
//			send_email();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return;
	}

	private static void send_email() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		String to = "654652424@qq.com";
		String subject = "subject";
		String content = "content";
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.qq.com");
		properties.put("mail.smtp.port", "587"); //465鎴�587
		properties.put("mail.smtp.auth", "true");
		Authenticator authenticator = new Email_Authenticator("654652424@qq.com", "xxxxxxx"); //鏀逛负姝ｇ‘鐨勯偖绠卞湴鍧�鍜岀嫭绔嬪瘑鐮�
		Session sendMailSession = Session.getDefaultInstance(properties, authenticator);
		MimeMessage mailMessage = new MimeMessage(sendMailSession);
		mailMessage.setFrom(new InternetAddress("654652424@qq.com"));
		// Message.RecipientType.TO灞炴�ц〃绀烘帴鏀惰�呯殑绫诲瀷涓篢O
		mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		mailMessage.setSubject(subject, "UTF-8");
		mailMessage.setSentDate(new Date());
		// MiniMultipart绫绘槸涓�涓鍣ㄧ被锛屽寘鍚玀imeBodyPart绫诲瀷鐨勫璞�
		Multipart mainPart = new MimeMultipart();
		// 鍒涘缓涓�涓寘鍚獺TML鍐呭鐨凪imeBodyPart
		BodyPart html = new MimeBodyPart();
		html.setContent(content.trim(), "text/html; charset=utf-8");
		mainPart.addBodyPart(html);
		mailMessage.setContent(mainPart);
		Transport.send(mailMessage);
		logger.info("OK!!!!");
	}

	static class Email_Authenticator extends Authenticator {
		String userName = null;
		String password = null;

		public Email_Authenticator() {
		}

		public Email_Authenticator(String username, String password) {
			this.userName = username;
			this.password = password;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(userName, password);
		}
	}
}
