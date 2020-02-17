import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TopServlet extends HttpServlet {

	private String _hostname = null;
	private String _dbname = null;
	private String _username = null;
	private String _password = null;

	public void init() throws ServletException {
		// iniファイルから自分のデータベース情報を読み込む
		String iniFilePath = getServletConfig().getServletContext()
				.getRealPath("WEB-INF/le4db.ini");
		try {
			FileInputStream fis = new FileInputStream(iniFilePath);
			Properties prop = new Properties();
			prop.load(fis);
			_hostname = prop.getProperty("hostname");
			_dbname = prop.getProperty("dbname");
			_username = prop.getProperty("username");
			_password = prop.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.println("<h3>ログイン</h3>");
		/*
		out.println("<form action=\"login\" method=\"GET\">");
		//遷移先は/login. 以下, name=で渡された文字列をgetParameterすることで遷移先で扱うことが出来る.
		 */
		out.println("<form action=\"userpage\" method=\"GET\">");
		//遷移先は/userpage.
		out.println("ユーザid： ");
		out.println("<input type=\"text\" name=\"login_id\"/>");
		out.println("<br/>");
		out.println("パスワード： ");
		out.println("<input type=\"password\" name=\"login_password\"/>");
		out.println("<br/>");
		out.println("<input type=\"submit\" value=\"ログイン\"/>");
		out.println("</form>");

		//out.println("<br/>");
		//out.println("<a href=\"list\">管理者用リスト</a>");

		out.println("</body>");
		out.println("</html>");
	}


	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
