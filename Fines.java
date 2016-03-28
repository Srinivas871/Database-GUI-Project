import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class Fines {
	Connection con=null;
	JFrame FinesScreen;
	JTable table_fines;
	String[][] table_fines_res =new String[1000][];
	int linecount=0;
	String search_res;
	ListSelectionModel model;
	int selectedRow=-1;
	
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		Fines fi=new Fines();
		fi.updateRegularFines();
		fi.FinesDisplay();
	} */
	
	public void  updateRegularFines()
	{
		try
		{
		con= DriverManager.getConnection("jdbc:mysql://localhost:3306/library?autoReconnect=true&useSSL=false","root","Srini@871");
		PreparedStatement Ids_cnt,Ids_stmt,loan_present_statement,loan_deter_stmt,fine_amt_stmt;
		ResultSet result;
		String loan_ids_stmt="";
		ArrayList<String> LoanIds =new ArrayList<>();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		   //get current date time with Date()
		   Date current_day = new Date();
		
		String loan_id_cnt_stmt= "select count(Loan_id)from book_loans where (Date_in is NULL and Due_date < '"
		   +dateFormat.format(current_day)+"') or (Date_in>Due_date);";
		Ids_cnt= con.prepareStatement(loan_id_cnt_stmt);
		result=Ids_cnt.executeQuery();
		
		while(result.next())
		{
			if(!result.getString(1).equals("0"))
			{
				loan_ids_stmt="select Loan_id from book_loans where (Date_in is NULL and Due_date < '"
						+dateFormat.format(current_day)+"') or (Date_in>Due_date);";
				System.out.println(loan_ids_stmt+" -- "+result.getString(1));
			}
			else
			{
				System.out.println("Count is 0");
			}
		}
		
		Ids_stmt=con.prepareStatement(loan_ids_stmt);
		result=Ids_stmt.executeQuery();
		
		while(result.next())
			LoanIds.add(result.getString(1));
		
		boolean present=false;
		
		for(String loan_id_one: LoanIds)
		{
			System.out.println(loan_id_one);
			loan_present_statement=con.prepareStatement("select count(*) from fines where loan_id ="+loan_id_one+";");
			result=loan_present_statement.executeQuery();
			
			while(result.next())
			{
				if(result.getString(1).equals("0"))
					present=false;
				else
					present=true;
			}
			
			if(present==true)
			{
				loan_deter_stmt=con.prepareStatement("select loan_id,fine_amt,paid from fines where loan_id ="+loan_id_one+";");
				result=loan_deter_stmt.executeQuery();
				
				ResultSetMetaData rsmd = result.getMetaData();
				int count= rsmd.getColumnCount();
				
				while(result.next())
				{
					String paid_value=result.getString(3);
					if(paid_value.equals("1"))
					{
						//do nothing
					}
					else
					{
						String ret_date_in="select date_in,Due_date from book_loans where loan_id="+loan_id_one+";";
						fine_amt_stmt=con.prepareStatement(ret_date_in);
						result=fine_amt_stmt.executeQuery();
						Date date_from=new Date();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						
						while(result.next())
						{
							String date_in_value=result.getString(1);
							if(date_in_value==null)
							{
								//temporary date_in is Due Date for calculation
								date_from=formatter.parse(result.getString(2));
							}
						}	
							
						long diff = current_day.getTime() - date_from.getTime();	
						diff=diff/(1000 * 60 * 60 * 24);
						System.out.println(diff);
						
						String fine_amt_update="update fines set fine_amt="+diff*(0.25) +" where loan_id="+loan_id_one+";";
						fine_amt_stmt=con.prepareStatement(fine_amt_update);
						fine_amt_stmt.executeUpdate();
					}
				}
			}
		}
		
		
		}catch(SQLException se)
		{
			se.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void FinesDisplay()
	{
		table_fines=null;
		String[] columnNames={"Card_No","fine_amount", "Paid"};
		
		InitialDisplay();
		
		System.out.println("linecount "+linecount);
		
		String[][] table_res1=new String[linecount][];
		
		for(int i=0;i<linecount;i++)
			table_res1[i]=table_fines_res[i];
		
		Object [] [] data = table_res1;
		
		table_fines=new JTable(data,columnNames);
		table_fines.setPreferredScrollableViewportSize(new Dimension(500,500));
		table_fines.setFillsViewportHeight(true);
		
		model=table_fines.getSelectionModel();	
		
		JScrollPane scpane= new JScrollPane(table_fines);
		JPanel p_center_table= new JPanel();
		JPanel p_south= new JPanel();
		p_center_table.add(scpane);
		
		JButton paybt=new JButton("Pay");
		paybt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		p_south.add(paybt);
		
		FinesScreen=new JFrame();
		FinesScreen.setSize(600,600);
		FinesScreen.getContentPane().add(BorderLayout.CENTER,p_center_table);
		FinesScreen.getContentPane().add(BorderLayout.SOUTH,p_south);
		
		FinesScreen.setVisible(true);
		
		
		 model.addListSelectionListener( new ListSelectionListener(){
	 			@Override
	 			public void valueChanged(ListSelectionEvent e) {
	 				// TODO Auto-generated method stub
	 				if(!model.isSelectionEmpty())
	 				{
	 				selectedRow=model.getMinSelectionIndex();
	 				System.out.println("Row Selected: "+selectedRow);
	 				
	 				/*if(table_res[selectedRow][6].equals("0"))
	 				{
	 					JFrame created_info=null;
	 					JOptionPane.showMessageDialog(created_info,"No Available copies for this branch");
	 				}*/
	 				
	 				}    }
				});
		
		 paybt.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//throw error for no selection
				if(selectedRow==-1)
				{
					JFrame created_info=null;
					JOptionPane.showMessageDialog(created_info,"Select a row to check out");
				}
				
				else if(table_fines_res[selectedRow][2].equals("True"))
				{
					JFrame created_info=null;
					JOptionPane.showMessageDialog(created_info,"Fine has been paid for the selection");
				}
				
				else
				{
					PayFine(table_fines_res[selectedRow][0]);
				}
				
			}
			 
		 });
		
		//FinesScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void InitialDisplay()
	{
		PreparedStatement main_stmt;
		ResultSet result;
		try
		{
		main_stmt=con.prepareStatement("select bl.card_no,'^',sum(f.fine_amt),'^',f.paid from book_loans bl join Fines f on bl.loan_id=f.loan_id group by bl.card_no,f.paid;");
		
		result=main_stmt.executeQuery();
		
		ResultSetMetaData rsmd = result.getMetaData();
		int count= rsmd.getColumnCount();
		//System.out.println(count);
		
		//getting column names
		for (int i = 1; i <= count; i++)
		{
		  // System.out.print(rsmd.getColumnLabel(i) + " ");  
		   search_res=search_res+rsmd.getColumnLabel(i)+"\t";
		}
		
		while(result.next())
		{
		    //System.out.println(result.getString(1)+" "+result.getString(2));
			//get 1st and 2nd columns
			String sql_row="";
		   for(int i=1; i<=count; i++)
			{
				//System.out.print(result.getString(i)+" ");
				search_res=search_res+result.getString(i);
				sql_row=sql_row+result.getString(i);
			}
		
		   if(result.getString(count).equals("1"))
		   {
			   sql_row=sql_row.substring(0,sql_row.length()-1)+"True";
		   }
		   else
		   {
			   sql_row=sql_row.substring(0,sql_row.length()-1)+"False";
		   }
		   
		   table_fines_res[linecount]=new String[7];
		   table_fines_res[linecount]=sql_row.split("\\^",7);
		   search_res=search_res+"\n";
			linecount++;
			
		}
		}catch(SQLException se)
		{
			se.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	private void PayFine(String card_idno) {
		// TODO Auto-generated method stub
		
		PreparedStatement check_bk_status,update_bk_status;
		ResultSet result;
		ArrayList<String> loanids =new ArrayList<>();
		try{
			
		check_bk_status=con.prepareStatement("select date_in,loan_id from book_loans where Card_no='"+card_idno+"';");
		result=check_bk_status.executeQuery();
		
		while(result.next())
		{
			String date_in_value=result.getString(1);
			if(date_in_value==null)
			{
				//do nothing and Display error message that the book hasn't been returned
				JFrame created_info=null;
				JOptionPane.showMessageDialog(created_info,"Book(s) taken by the user hasn't been returned");
				return;
			}
			else
			{
				//update the pay status in Fines Table
			update_bk_status=con.prepareStatement("update fines set pay=1 where loan_id="+result.getString(2)+";");
			update_bk_status.executeUpdate();
			}
		}
		
		}catch(SQLException se)
		{
			se.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		
}


