package post;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import post.Board;
public class Post {
	
	//Field
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	
		public Post() {
			try {
				//JDBC Driver 등록
				Class.forName("oracle.jdbc.OracleDriver");
				
				//연결하기
				conn = DriverManager.getConnection(
						"jdbc:oracle:thin:@localhost:1521/XE",
						"ezen",
						"12345"
						);
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}

		//Method
		public void list() {
				
				//타이틀 및 컬럼명 출력
				System.out.println();
				System.out.println("[게시물 목록]");
				System.out.println("---------------------------------------------");
				System.out.printf("%-6s%-12s%-16s%-40s\n", "NO","작성자","날짜","제목");
				System.out.println("---------------------------------------------");
				
//				
				//boards 테이블에서 게시물 정보를 가져와ㅏ서 출력하기
				try {
					String sql = "" +
							"SELECT bno, btitle, bcontent, bwriter, bdate " +
							"FROM boards " +
							"ORDER BY bno DESC";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					ResultSet rs = pstmt.executeQuery();
					while(rs.next()) {
						Board board = new Board();
						board.setBno(rs.getInt("bno"));
						board.setBtitle(rs.getString("btitle"));
						board.setBcontent(rs.getString("bcontent"));
						board.setBwriter(rs.getString("bwriter"));
						board.setBdate(rs.getDate("bdate"));
						System.out.printf("%-6s%-12s%-16s%-40s\n",
						board.getBno(),
						board.getBwriter(),
						board.getBdate(),
						board.getBtitle());
					}
					rs.close();
					pstmt.close();
				} catch(SQLException e) {
					e.printStackTrace();
					exit();
				}
				
				//메인 메뉴 출력
				mainMenu();
		}
		
		public void mainMenu() {
			System.out.println();
			System.out.println("---------------------------------------------");
			System.out.println("메인 메뉴: 1.게시물 추가하기 | 2.게시물 읽기 | 3.게시물 전체 삭제 | 4.종료");
			System.out.print("메뉴 선택: ");
			String menuNo = scanner.nextLine();
			System.out.println();
			
			switch(menuNo) {
			case "1" : create(); break;
			case "2" : read(); break;
			case "3" : clear(); break;
			case "4" : exit();
			}
		}
		
		public void create() {
			Board board = new Board();
			System.out.println("[새 게시물 입력]");
			System.out.print("제목: ");
			board.setBtitle(scanner.nextLine());
			System.out.print("내용: ");
			board.setBcontent(scanner.nextLine());
			System.out.print("작성자: ");
			board.setBwriter(scanner.nextLine());
			
			System.out.println("---------------------------------------------");
			System.out.println("보조 메뉴: 1.작성완료 | 2.취소");
			System.out.println("메뉴 선택");
			String menuNo = scanner.nextLine();
			if(menuNo.equals("1")) {
				try {
					String sql = "" +
							"insert into boards (bno, btitle, bcontent, bwriter, bdate) " +
							"values (SEQ_BNO.NEXTVAL, ?,?,?,sysdate)";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, board.getBtitle());
					pstmt.setString(2, board.getBcontent());
					pstmt.setString(3, board.getBwriter());
					pstmt.executeUpdate();
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			list();
		}
		public void read() {	
			System.out.println("[게시물 읽기]");
			System.out.println("게시물 번호(숫자): ");
			int bno = Integer.parseInt(scanner.nextLine());
			
			try {
				String sql = "" +
							"SELECT bno, btitle, bcontent, bwriter, bdate " +
							"FROM boards " +
							"WHERE bno=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, bno);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) {
					Board board = new Board();
					board.setBno(rs.getInt("bno"));
					board.setBtitle(rs.getString("btitle"));
					board.setBcontent(rs.getString("bcontent"));
					board.setBwriter(rs.getString("bwriter"));
					board.setBdate(rs.getDate("bdate"));
					System.out.println("###########");
					System.out.println("번호: " + board.getBno());
					System.out.println("제목: " + board.getBtitle());
					System.out.println("내용: " + board.getBcontent());
					System.out.println("작성자: " + board.getBwriter());
					System.out.println("날짜: " + board.getBdate());
					//보조 메뉴 출력
					System.out.println("---------------------------------------------");
					System.out.println("보조 메뉴: 1.수정하기 | 2.삭제하기 | 3.게시물 보기");
					System.out.println("메뉴선택: ");
					String menuNo = scanner.nextLine();
					
					if(menuNo.equals("1")) {
						update(board);
					}else if(menuNo.equals("2")) {
						delete(board);
					}
					rs.close();
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
			list();
		}
		//게시글 수정
		public void update(Board board) {
			//수정 내용 입력 받기
			System.out.println("[수정 내용 입력]");
			System.out.println("제목: ");
			board.setBtitle(scanner.nextLine());
			System.out.println("내용: ");
			board.setBcontent(scanner.nextLine());
			System.out.println("작성자: ");
			board.setBwriter(scanner.nextLine());
			
			//보조메뉴 출력
			System.out.println("---------------------------------------------");
			System.out.println("보조메뉴: 1.수정하기 | 2.수정취소");
			System.out.println("메뉴 선택: ");
			String menuNo = scanner.nextLine();
			if(menuNo.equals("1")) {
				try {
					String sql= "" +
								"UPDATE boards SET btitle=?, bcontent=?, bwriter=? " +
								"WHERE bno=?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, board.getBtitle());
					pstmt.setString(2, board.getBcontent());
					pstmt.setString(3, board.getBwriter());
					pstmt.setInt(4, board.getBno());
					pstmt.executeUpdate();
					pstmt.close();
				}catch (Exception e) {
					e.printStackTrace();
					exit();
				}
			}
		}
		public void delete(Board board) {
			try {
				String sql = "DELETE FROM boards WHERE bno=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, board.getBno());
				pstmt.executeUpdate();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
			//게시물 목록
			list();
		}
		//boards 테이블에 게시물 정보 전체 삭제
		public void clear() {		
			System.out.println("[게시물 전체 삭제]");
			System.out.println("---------------------------------------------");
			System.out.println("보조 메뉴: 1.삭제하기 | 2.취소");
			System.out.println("메뉴 선택: ");
			String menuNo = scanner.nextLine();
			Board board = new Board();
			System.out.println(board.getBno());
			if(menuNo.equals("1")) {
				try {								
					String sql1 = "drop sequence seq_bno";
					String sql2 = "TRUNCATE TABLE boards";
					String sql3 = "create sequence SEQ_BNO nocache";
					
					PreparedStatement pstmt1 = conn.prepareStatement(sql1);
					pstmt1.executeUpdate();
					pstmt1.close();
					PreparedStatement pstmt2 = conn.prepareStatement(sql2);
					pstmt2.executeUpdate();
					pstmt2.close();
					PreparedStatement pstmt3 = conn.prepareStatement(sql3);
					pstmt3.executeUpdate();
					pstmt3.close();
				}catch (Exception e) {
					e.printStackTrace();
					exit();
				}
			}
			
			list();
			
		}
		public void exit() {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
			System.exit(0);
		}
	public static void main(String[] args) {
		Post boardExample = new Post();
		boardExample.list();

	}

}
