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
public class ItemServlet extends HttpServlet {

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

		String bookid = request.getParameter("bookid");

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>更新</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<form action=\"update\" method=\"GET\">");
			//out.println("商品ID： " + pid);
			out.println("本ID： " + bookid);
			//out.println("<input type=\"hidden\" name=\"update_pid\" + value=\"" + pid + "\"/>");
			out.println("<input type=\"hidden\" name=\"update_bookid\" + value=\"" + bookid + "\"/>");
			out.println("<br/>");

			//ResultSet rs = stmt.executeQuery("SELECT * FROM products WHERE pid = " + pid);
			ResultSet rs = stmt.executeQuery("SELECT * FROM books WHERE bookid = " + bookid);
			while (rs.next()) {
				String title = rs.getString("title");
				String author = rs.getString("author");
				String publisher = rs.getString("publisher");

				out.println("タイトル： ");
				out.println("<input type=\"text\" name=\"update_title\" value=\"" + title + "\"/>");
				out.println("<br/>");
				out.println("作者： ");
				out.println("<input type=\"text\" name=\"update_author\" value=\"" + author + "\"/>");
				out.println("<br/>");
				out.println("出版社： ");
				out.println("<input type=\"text\" name=\"update_publisher\" value=\"" + publisher + "\"/>");
				out.println("<br/>");

			}
			rs.close();

			out.println("<input type=\"submit\" value=\"更新\"/>");
			out.println("</form>");

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

		out.println("<h3>削除</h3>");
		out.println("<form action=\"delete\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"delete_bookid\" value=\"" + bookid + "\">");
		out.println("<input type=\"submit\" value=\"削除\"/>");
		out.println("</form>");

		out.println("<br/>");
		out.println("<a href=\"list\">管理者用ページに戻る</a>");

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
