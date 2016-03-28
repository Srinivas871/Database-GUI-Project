import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class CheckOut {

	JLabel Book_label,isbn_label,branch_label,Available_copies,Card_label;
	JTextField Card_no_field;
	JButton submit;
	JTextField mand_instr;
	JPanel p_center,p_south;
	JFrame checkOutFrame;
	JButton CheckOutBt;
	Connection con=null;
	boolean checkedOutTrue=false;
	
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		CheckOut co=new CheckOut();
		co.checkOutBook("0671877135", "1", "In Celebration Of Lammas Night", "1");
	}*/

	public void checkOutBook(String book_idno, String brid, String book_name, String avail_copies)
	{
		JPanel p_card=new JPanel();
		
		System.out.println("Selected Book");
		System.out.println("ISBN: "+book_idno);
		System.out.println("Branch_id: "+brid);
		System.out.println("Book_name: "+book_name);
		System.out.println("Available_Copies: "+avail_copies);
		
		
		Book_label=new JLabel("Book            : "+book_name);
		isbn_label=new JLabel("ISBN             : "+book_idno);
		branch_label=new JLabel("Branch_ID  : "+brid);
		Available_copies=new JLabel("Available no of copies :"+avail_copies);
		Card_label=new JLabel("                 Card_no        : ");
		Card_no_field=new JTextField();
		CheckOutBt = new JButton("Check out");
		
		p_card.setLayout(new BoxLayout(p_card,BoxLayout.X_AXIS));
		p_card.add(Card_label);
		p_card.add(Card_no_field);
		
		p_center=new JPanel();
		p_center.setLayout(new BoxLayout(p_center,BoxLayout.Y_AXIS));
		
		p_center.add(Book_label);
		p_center.add(isbn_label);
		p_center.add(branch_label);
		p_center.add(Available_copies);
		p_center.add(p_card);
		
		p_south=new JPanel();
		p_south.setLayout(new BoxLayout(p_south,BoxLayout.Y_AXIS));
		
		CheckOutBt.setAlignmentX( CheckOutBt.CENTER_ALIGNMENT);
		p_south.add(CheckOutBt);
		checkOutFrame= new JFrame();
		checkOutFrame.getContentPane().add(BorderLayout.CENTER,p_center);
		checkOutFrame.getContentPane().add(BorderLayout.SOUTH,p_south);
		checkOutFrame.setSize(300,150);
		checkOutFrame.setVisible(true);
		
		final String book_id=book_idno;
		final String branch_id=brid;
		
		CheckOutBt.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				updateBook_Copies(book_id,branch_id,Card_no_field.getText().trim());
				
				if(checkedOutTrue)
				{
				JFrame created_info=null;
				JOptionPane.showMessageDialog(created_info,"Book Checked-Out Succesfully");
				checkOutFrame.dispose();
				}
			}
			
		});
		
		//checkOutFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void updateBook_Copies(String book_idno, String brid,String card_idno)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date current_day=new Date();
		try{
		con= DriverManager.getConnection("jdbc:mysql://localhost:3306/library?autoReconnect=true&useSSL=false","root","Srini@871");
		PreparedStatement book_copies_stmt,book_copies_new_stmt,user_books_checkout;
		ResultSet result;
		String Loan_max_id="";
		int next_id,checked_out_books_cnt=0;
		
		user_books_checkout=con.prepareStatement("select count(*) from book_loans where Card_no='"+card_idno
				+"' and (Date_in>'"+dateFormat.format(current_day)+"' or Date_in is NULL);");
		
		result= user_books_checkout.executeQuery();
		
		while(result.next())
			checked_out_books_cnt=Integer.parseInt(result.getString(1));
		
		if(checked_out_books_cnt>=3)
		{
			JFrame created_info=null;
			JOptionPane.showMessageDialog(created_info,"User already has three books, Can't Checkout further");
			checkOutFrame.dispose();
			return;
		}
		
		book_copies_stmt=con.prepareStatement("select max(Loan_id) from book_loans;");
		result= book_copies_stmt.executeQuery();
		
		while(result.next())
			Loan_max_id=result.getString(1);
		
		next_id=Integer.parseInt(Loan_max_id)+1;
		
		   //get current date time with Date()
		   Date Date_out = new Date();
		   System.out.println("Current Date "+dateFormat.format(Date_out));
		
		   Calendar cal = Calendar.getInstance(); 
		   //adding 14 days to current date cal.add(Calendar.DAY_OF_MONTH, 1);

		   cal.add(Calendar.DAY_OF_MONTH, 14);
		   Date Date_due=cal.getTime();
		   System.out.println("Due Date "+dateFormat.format(Date_due));
		
		   String sqlstatmnt="insert into book_loans (Loan_id,Isbn,Branch_id,Card_no,Date_out,Due_date) "
					+ "values("+next_id+","+"'"+book_idno+"'"+","+brid+","+"'"+card_idno+"'"+","+"'"+dateFormat.format(Date_out)+"'"+","+"'"+dateFormat.format(Date_due)+"');";
		   
		   System.out.println(sqlstatmnt);
		   
		book_copies_new_stmt=con.prepareStatement(sqlstatmnt);
		
		book_copies_new_stmt.executeUpdate(); 
		
		checkedOutTrue=true;
		
		}catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
