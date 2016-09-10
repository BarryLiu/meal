package com.ragentek.mealsupplement.tools;

import com.ragentek.mealsupplement.poi.Attachment;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by zixiao.zhang on 2015/9/11.
 */
public class MailUtils {
	private static final boolean LOCAL_TEST = false;
	private String host = ""; // smtp服务器,这个一般不需要自己配置，使用公司的即可。当然，也可以使用第三方如qq(smtp.qq.com),126邮箱,此时用户名密码就是自己注册的邮箱名
	private String user = ""; // 用户名
	private String pwd = ""; // 密码
	private String from = ""; // 发件人地址
	private List<String> to = new ArrayList<String>(); // 收件人地址
	private String subject = ""; // 邮件标题
	private String url = ""; // 回到地址，邮件中会点击该地址重新访问服务器
	private String title;
	private String attach;
	private List<Attachment> attachments;
	private static Logger logger = Logger.getLogger(MailUtils.class);
	Pattern p = Pattern.compile(
			"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

	public MailUtils() {
		host = ConfigUtil.getProperty("Mail_host");
		user = ConfigUtil.getProperty("Mail_user");
		pwd = ConfigUtil.getProperty("Mail_pwd");
		from = ConfigUtil.getProperty("Mail_from");
		url = ConfigUtil.getProperty("Mail_url") + ":" + ConfigUtil.getProperty("Mail_port");
		title = ConfigUtil.getProperty("Mail_title");
		attach = ConfigUtil.getProperty("Mail_signatures");
		//logger.info(host + ":" + user + ":" + pwd + ":" + from + ":" + url + ":" + title);
		//logger.info(attach);
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	public void addAttachment(Attachment attachment) {
		if(attachments == null) {
			attachments = new ArrayList<Attachment>();
		}
		attachments.add(attachment);
	}

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
					if(!this.to.contains(tos[i])) {
						this.to.add(tos[i]);
					}
				}
			}
		}
		this.subject = subject;
	}
	
	public void setTos(List<String> tos) {
		if(tos != null && tos.size() > 0) {
			for(String to : tos) {
				if(to != null) {
					to = to.trim();
					if(!"".equals(to) && p.matcher(to).matches()) {
						if(!this.to.contains(to)) {
							this.to.add(to);
						}
					}
				}
			}
		}
	}
	
	public void addTo(String to) {
		if(to != null && !"".equals(to.trim()) && p.matcher(to.trim()).matches()) {
			if(!this.to.contains(to.trim())) {
				this.to.add(to);
			}
		}
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean send(String txt, String cc, String bcc) {
		return send(txt, cc, bcc, attachments);
	}
	public boolean send(String txt, String cc, String bcc, List<Attachment> attachments) {
		boolean res = false;
		// if(true)return;
		Properties props = new Properties();
		// 设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
		props.put("mail.smtp.host", host);
		// 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
		props.put("mail.smtp.auth", "true");
		// 用刚刚设置好的props对象构建一个session
		Session session = Session.getDefaultInstance(props);
		// 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
		// 用（你可以在控制台（console)上看到发送邮件的过程）
		session.setDebug(false);
		// 用session为参数定义消息对象
		MimeMessage message = new MimeMessage(session);
		try {
			// 加载发件人地址
			if(LOCAL_TEST) {
				message.setFrom(new InternetAddress(ConfigUtil.getProperty("Mail_local_from")));
			} else {
				message.setFrom(new InternetAddress(from));
			}
			// 加载收件人地址
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
			// 加载标题
			message.setSubject(title + subject);
			// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
			Multipart multipart = new MimeMultipart();

			// 设置邮件的文本内容
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(txt + attach, "text/html;charset=utf-8");
			multipart.addBodyPart(contentPart);

			MimeBodyPart mbp = null;
			if(attachments != null && attachments.size() > 0) {
				for(Attachment attachment : attachments) {
					String fileName = attachment.getFileName();
					String mimeType = attachment.getMimeType();
					if(!TextUtil.isNullOrEmpty(fileName) && !TextUtil.isNullOrEmpty(mimeType)) {
						InputStream inputStream = attachment.getInputStream();
						if(inputStream == null && attachment.getFile() != null) {
							File file = attachment.getFile();
							if(file.exists()) {
								inputStream = new FileInputStream(file);
							}
						}
						if(inputStream != null) {
							mbp = new MimeBodyPart(); //加入附件
							mbp.setDataHandler(new DataHandler(new ByteArrayDataSource(inputStream, mimeType))); //application/x-excel
							mbp.setFileName(new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
							multipart.addBodyPart(mbp);
						}
					}
				}
			}

			logger.info("CC:" + cc);

			logger.info("BCC:" + bcc);

			// 添加附件
			// BodyPart messageBodyPart = new MimeBodyPart();
			// DataSource source = new FileDataSource(affix);
			// 添加附件的内容
			// messageBodyPart.setDataHandler(new DataHandler(source));
			// 添加附件的标题
			// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
			// sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			// messageBodyPart.setFileName("=?GBK?B?"+
			// enc.encode(affixName.getBytes()) + "?=");
			// multipart.addBodyPart(messageBodyPart);

			// 将multipart对象放到message中
			message.setContent(multipart);
			// 保存邮件
			message.saveChanges();
			// 发送邮件
			Transport transport = session.getTransport("smtp");
			// 连接服务器的邮箱
			if(LOCAL_TEST) {
				transport.connect(ConfigUtil.getProperty("Mail_local_host"), ConfigUtil.getProperty("Mail_local_user"), ConfigUtil.getProperty("Mail_local_pwd"));
			} else {
				String password = SerializeUtil.getMailPwd();
				if(TextUtil.isNullOrEmpty(password)) {
					password = pwd;
				}
//				user ="yingjing.liu@wheatek.com";
//				password = "Wheatek#";

				System.out.println("mail connection: host="+host+",user="+user+",password="+password+" tosize:"+to.size());
				transport.connect(host, user, password);
			}

			// 把邮件发送出去
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			logger.info("mail send success");
			res = true;
		} catch (Exception e) {
			logger.error("mail send error", e);
			e.printStackTrace();
		}
		return res;
	}

	private String getUrl() {
		return url;
	}
	
	/**
	 * 通过getServerName和getServerPort或者是从requestURL中截取可以获得请求的ip和端口，而且如果ip是域名可以获得域名，但是这样如果访问时是localhost或直接ip就也是localhost或ip，在localhost下发出邮件，收到后还是localhost这肯定不行
	 * 在ip下发出邮件，万一邮件发出后更改了部署服务器，也是有风险的
	 * 通过LocalHost来获得的首先域名都获不得，只能获得ip，同样有ip情况下发邮件的风险
	 * 综合来说，目前还无法综合处理各种情况，不过考虑到后续肯定会使用域名来访问，故目前采取如下的策略
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
		if(url == null) {
			url = getUrl();
		}
		return url;
	}

	public static void main(String[] args) {
		 MailUtils mailUtil = new MailUtils();
		 mailUtil.setAddress("subject", "1187597904@qq.com");
		 mailUtil.send("hahha", "", "");
		/*try {
			send_email();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	private static void send_email() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		String to = "zixiao.zhang@wheatek.com";
		String subject = "subject";
		String content = "content";
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.qq.com");
		properties.put("mail.smtp.port", "25");
		properties.put("mail.smtp.auth", "true");
		Authenticator authenticator = new Email_Authenticator("654652424@qq.com", "xxxxxx");
		Session sendMailSession = Session.getDefaultInstance(properties, authenticator);
		MimeMessage mailMessage = new MimeMessage(sendMailSession);
		mailMessage.setFrom(new InternetAddress("654652424@qq.com"));
		// Message.RecipientType.TO属性表示接收者的类型为TO
		mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		mailMessage.setSubject(subject, "UTF-8");
		mailMessage.setSentDate(new Date());
		// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		Multipart mainPart = new MimeMultipart();
		// 创建一个包含HTML内容的MimeBodyPart
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

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(userName, password);
		}
	}
}
