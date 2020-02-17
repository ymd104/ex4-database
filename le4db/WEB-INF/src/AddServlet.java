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
public class AddServlet extends HttpServlet {

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

		//String addName = request.getParameter("add_name");
		String addTitle = request.getParameter("add_title");
		//String addPrice = request.getParameter("add_price");
		String addAuthor = request.getParameter("add_author");
		String addPublisher = request.getParameter("add_publisher");

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			int max_bookid = 0;
			ResultSet rs = stmt.executeQuery("SELECT MAX(bookid) AS max_bookid FROM books");
			while (rs.next()) {
				max_bookid = rs.getInt("max_bookid");

			}
			rs.close();

			int addBookID = max_bookid + 1;
			stmt.executeUpdate("INSERT INTO books VALUES(" + addBookID + ", '" + addTitle + "', '" + addAuthor + "', '" + addPublisher + "')");

			out.println("以下の本を追加しました<br/><br/>");
			out.println("本ID: " + addBookID + "<br/>");
			out.println("タイトル: " + addTitle + "<br/>");
			out.println("作者: " + addAuthor + "<br/>");
			out.println("出版社：" + addPublisher + "<br/>");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		out.println("<br/>");
		out.println("<a href=\"list\">トップページに戻る</a>");

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
