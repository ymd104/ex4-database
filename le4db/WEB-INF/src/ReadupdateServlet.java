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
public class ReadupdateServlet extends HttpServlet {

	private String _hostname = null;
	private String _dbname = null;
	private String _username = null;
	private String _password = null;

	public boolean isNum(String str) {
		int tmp;
	    try {
	        tmp = Integer.parseInt(str);
	        return tmp <= 0;
	    } catch (NumberFormatException e) {
	        return true;
	    }
	}

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

		String readerID = request.getParameter("readerid");
		String password = request.getParameter("password");

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<body>");

		String updateBookID = request.getParameter("update_bookid");
		String updateHistory = request.getParameter("update_history");
		String updateEvaluation = request.getParameter("update_evaluation");

		if(isNum(updateBookID)) {
			out.println("Error 1以上の整数値を入力してください。");
		}

		else {

		Connection conn = null;
		Statement stmt = null;
		int records = 0;
		int evaltmp = 0;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			readerID = readerID.substring(3, readerID.length()-2);

			//updateBookIDがread[readerid]にそもそも含まれるか, updateHistoryが読了か積読のどちらかであるか, 評価が0から5のいずれかであるかを確かめる. 全ての条件を満たすなら更新する.

			if(!(updateBookID.equals("") || updateHistory.equals("") || updateEvaluation.contentEquals("")))
			{

			if( (updateHistory.equals("読了") || updateHistory.equals("積読") || updateHistory.equals("未読")) && (0 <= evaltmp || evaltmp <= 5)) {

			ResultSet rs = stmt.executeQuery("SELECT COUNT (*) FROM read" + readerID + " WHERE bookid = " + updateBookID);
			while (rs.next()) {
				records = rs.getInt("count");
			}
			rs.close();


			evaltmp = Integer.parseInt(updateEvaluation);

			if(records == 1) {


			if(updateHistory.equals("未読")) {

			stmt.executeUpdate("DELETE FROM readhistory  WHERE readerID = " + readerID + " AND bookid = " + updateBookID);

			out.println("以下の本を削除しました。<br/><br/>");
			out.println("本ID: " + updateBookID + "<br/>");
			out.println("読書歴: " + updateHistory + "<br/>");
			out.println("評価: " + updateEvaluation + "<br/>");

			}else {

			stmt.executeUpdate("UPDATE read" + readerID + " SET history = '" + updateHistory + "', evaluation = '" + evaltmp + "' WHERE bookid = " + updateBookID);

			out.println("以下の本を更新しました。<br/><br/>");
			out.println("本ID: " + updateBookID + "<br/>");
			out.println("読書歴: " + updateHistory + "<br/>");
			out.println("評価: " + updateEvaluation + "<br/>");
			}


			}else {
			stmt.executeUpdate("INSERT INTO readhistory VALUES(" + updateBookID + ", " + readerID +  ",'" + updateHistory + "', " + updateEvaluation + ")");

			out.println("以下の本を追加しました。<br/><br/>");
			out.println("本ID: " + updateBookID + "<br/>");
			out.println("読書歴: " + updateHistory + "<br/>");
			out.println("評価: " + updateEvaluation + "<br/>");

			}

			}else {
			out.println("Error データが不正です。");
			}
			}
			else
			{out.println("Error 全てのフォームに入力する必要があります。");}



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
		//out.println("<a href=\"userpage?login_id=" + Integer.parseInt(readerID.substring(3, readerID.length()-2)) + "&login_password=" + password.substring(3, password.length()) + "\">ユーザページに戻る</a>");
		out.println("<a href=\"userpage?login_id=" + Integer.parseInt(readerID) + "&login_password=" + password.substring(3, password.length()) + "\">ユーザページに戻る</a>");
		out.println("</body>");
		out.println("</html>");
	}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
