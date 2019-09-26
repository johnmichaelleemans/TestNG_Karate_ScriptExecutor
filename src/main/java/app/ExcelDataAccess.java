package app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;


/*******************************TestNG Testing Framework Tool ************************************
 *******************************      Author:    ***********************************
 *******************************John-Michael Leemans********************************
 */


public class ExcelDataAccess {
	

	private final String path;
	private String datasheetName;

	public String getDatasheetName() {
		return datasheetName;
	}


	public void setDatasheetName(String datasheetName) {
		this.datasheetName = datasheetName;
	}


	public ExcelDataAccess(String filePath, String fileName) {

		String sep = Util.getFileSeparator();
		this.path = filePath + sep + fileName ;

	}

	public Boolean checkFileExists(){
		Boolean fileCheck = new java.io.File(this.path).isFile();
		return fileCheck;
	}


	public String getValue(int rowMinusOne, int columnMinusOne) throws IOException {

		DataFormatter formatter = new DataFormatter();
		String value = null;
		FileInputStream fileIn = null;
		//add evaluator 
		FormulaEvaluator evaluator;

		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheet(this.datasheetName);
			//add evaluator
			evaluator = wb.getCreationHelper().createFormulaEvaluator();
			//close
			wb.close();
			HSSFRow row = sheet.getRow(rowMinusOne);


			Cell cell = row.getCell(columnMinusOne);
			if( cell != null ){
		
				value = formatter.formatCellValue(cell,evaluator);

			}

			else
				value = "";

		}

		finally {
			if (fileIn != null)
				fileIn.close();
		}

		return value;
	}



	public int getColumnNum(String column_name, int row_num) throws IOException, FrameworkException {
		
		DataFormatter formatter = new DataFormatter();

		int columnNum = -1;
		FileInputStream fileIn = null;

		try
		{


			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);

			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);
			HSSFRow row = sheet.getRow(row_num);
			Iterator <Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				if(formatter.formatCellValue(cell).equals(column_name)){
					columnNum = cell.getColumnIndex();
					break;
				}
			}
			workb.close();

		} 
		catch(Exception ex){
			throw new FrameworkException(ex,  this.datasheetName) ;


		}
		finally {
			if (fileIn != null)
				fileIn.close();
		}

		return columnNum;
	}


	public int getRowNum(String strObjName, int intObjCol) throws IOException {

		int rowNum = -1;
		FileInputStream fileIn = null;
		
		DataFormatter formatter = new DataFormatter();

		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);
		
			Iterator <Row> rows     = sheet.rowIterator();
			while (rows.hasNext()) {
				Row row = rows.next();
				Cell cell = row.getCell(intObjCol);
				if(cell != null){
					
					if(formatter.formatCellValue(cell).equals(strObjName)){
						rowNum = row.getRowNum();
						break;
					}
				}
			}
			workb.close();

		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return rowNum;
	}


	public List<Integer> getRunNums(int intRunCol) throws IOException {

		int rowNum = 0;
		List<Integer> runList = new ArrayList<Integer>();
		FileInputStream fileIn = null;
		DataFormatter formatter = new DataFormatter();

		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);
		
			Iterator <Row> rows     = sheet.rowIterator();
			while (rows.hasNext()) {
				Row row = rows.next();
				Cell cell = row.getCell(intRunCol);
				
				if(formatter.formatCellValue(cell).equalsIgnoreCase("Yes")){	
					rowNum = row.getRowNum();
					runList.add(rowNum);
				}
			}
			workb.close();

		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return runList;
	}


	public List<List<String>> getRunNames(List<Integer> intRowNums, int columnNum, int intIterationCol, int intStartCol, int intEndCol, int tsColNum, int appColNum) throws IOException {


		//add list of lists
		List<List<String>> listRunList = new  ArrayList<List<String>>();

		FileInputStream fileIn = null;
		DataFormatter formatter = new DataFormatter();

		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);
			
			Iterator <Integer> rows     = intRowNums.iterator();
			while (rows.hasNext()) {
				List<String> runList = new ArrayList<String>();
				int row = rows.next();
				HSSFRow Hrow = sheet.getRow(row);
				Cell cell = Hrow.getCell(columnNum);
			
				runList.add(formatter.formatCellValue(cell));

				cell = Hrow.getCell(intIterationCol);
				
				runList.add(formatter.formatCellValue(cell));

				cell = Hrow.getCell(intStartCol);
				
				runList.add(formatter.formatCellValue(cell));

				cell = Hrow.getCell(intEndCol);
				
				runList.add(formatter.formatCellValue(cell));

				cell = Hrow.getCell(tsColNum);
				
				runList.add(formatter.formatCellValue(cell));

				cell = Hrow.getCell(appColNum);
				
				runList.add(formatter.formatCellValue(cell));

				listRunList.add(runList);
			}

			workb.close();

		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return listRunList;

	}


	public List<String> getFlow(int tc_column, int row_num) throws IOException {

		List<String> MethodList = new ArrayList<String>();
		FileInputStream fileIn = null;
		DataFormatter formatter = new DataFormatter();

		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);
			HSSFRow row = sheet.getRow(row_num);
			Iterator <Cell> cellIterator =  row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
			
				if(cell.getColumnIndex() == tc_column || formatter.formatCellValue(cell) == ""){
					continue;
				}
				
				MethodList.add(formatter.formatCellValue(cell));
			}
			workb.close();


		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return MethodList;
	}


	public List<String> getAllKeys(int tc_column, int row_num) throws IOException {
		List<String> MethodList = new ArrayList<String>();
		FileInputStream fileIn = null;
		DataFormatter formatter = new DataFormatter();

		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);
			HSSFRow row = sheet.getRow(row_num);
			Iterator <Cell> cellIterator =  row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
		
				if(formatter.formatCellValue(cell) == ""){
					continue;
				}
				
				MethodList.add(formatter.formatCellValue(cell));
			}
			workb.close();


		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return MethodList;
	}


	public int getRowNumIteration(String name, int tc_colNum, String iteration, int it_colNum) throws IOException {
		int rowNum = -1;

		FileInputStream fileIn = null;
		DataFormatter formatter = new DataFormatter();

		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);

			Iterator <Row> rows     = sheet.rowIterator();
			while (rows.hasNext()) {
				Row row = rows.next();
				Cell cell = row.getCell(tc_colNum);
				Cell cellIt = row.getCell(it_colNum);
				
				if(formatter.formatCellValue(cell).equals(name) && formatter.formatCellValue(cellIt).equals(iteration)){
					rowNum = row.getRowNum();
					break;
				}
			}
			workb.close();

		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return rowNum;
	}

	public int addRow() throws IOException{
		FileInputStream fileIn = null;
		fileIn = new FileInputStream(this.path);
		POIFSFileSystem fs = new POIFSFileSystem(fileIn);
		HSSFWorkbook workb = new HSSFWorkbook(fs);
		HSSFSheet sheet = workb.getSheet(this.datasheetName);

		int lastrownum = sheet.getLastRowNum() + 1;
		sheet.createRow(lastrownum);
		fileIn.close();
		FileOutputStream fileOut = new FileOutputStream(this.path);
		workb.write(fileOut);
		workb.close();
		if (fileIn != null)
			fileIn.close();

		return lastrownum;
	}
	public void  writeToExcel(int rownum, String columnheader, String Value) throws IOException, FrameworkException{
		FileInputStream fileIn = null;

		int icoloumnno= getColumnNum(columnheader, 1);

		try{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);


			
			HSSFRow row = sheet.getRow(rownum);
			HSSFCell cell = row.createCell(icoloumnno);

		
			cell.setCellType(CellType.STRING);
			cell.setCellValue(Value);

			fileIn.close();
			FileOutputStream fileOut = new FileOutputStream(this.path);
			workb.write(fileOut);
			

			workb.close();
		}
		finally {
			if (fileIn != null)
				fileIn.close();
		}
	}	

	public int AddColumn(int rowNum,String ColumnName) throws IOException{
		FileInputStream fileIn = null;
		int columnNum = -1;

		try{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);

			HSSFRow row = sheet.getRow(rowNum);
			HSSFCell cell = row.createCell(row.getLastCellNum());


		
			cell.setCellType(CellType.STRING);
			cell.setCellValue(ColumnName);
			cell.setCellStyle(row.getCell(cell.getColumnIndex() - 1).getCellStyle());

			columnNum = cell.getColumnIndex();
			fileIn.close();
			FileOutputStream fileOut = new FileOutputStream(this.path);
			workb.write(fileOut);
		
			workb.close();
		}
		finally {
			if (fileIn != null)
				fileIn.close();
		}
		return columnNum;
	}

	public void setValue(int rowNum, int colNum, String value) throws IOException{

		FileInputStream fileIn = null;

		try{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);

	
			HSSFRow row = sheet.getRow(rowNum);
			HSSFCell cell = row.getCell(colNum);

			if(cell == null){
				cell = row.createCell(colNum);
			}

		
			cell.setCellType(CellType.STRING);
			cell.setCellValue(value);

			fileIn.close();
			FileOutputStream fileOut = new FileOutputStream(this.path);
			workb.write(fileOut);
			//workb.write();

			workb.close();
		}
		finally {
			if (fileIn != null)
				fileIn.close();
		}

	}
	public void setValueCommentStatus(int rowNum, int colNum, String value, String commentText) throws IOException{

		FileInputStream fileIn = null;


		try{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);

			CreationHelper factory = workb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();

			HSSFRow row = sheet.getRow(rowNum);
			HSSFCell cell = row.getCell(colNum);

			if(cell == null){
				cell = row.createCell(colNum);
			}

			cell.setCellType(CellType.STRING);

			CellStyle styleGreen = workb.createCellStyle();
			styleGreen.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
			styleGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CellStyle styleRed = workb.createCellStyle();
			styleRed.setFillForegroundColor(IndexedColors.ROSE.getIndex());
			styleRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CellStyle styleOrange = workb.createCellStyle();
			styleOrange.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			styleOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);


			if(cell.getCellComment() == null){

				ClientAnchor anchor = factory.createClientAnchor();
				anchor.setCol1(cell.getColumnIndex());
				anchor.setCol2(cell.getColumnIndex()+3);
				anchor.setRow1(row.getRowNum());
				anchor.setRow2(row.getRowNum()+3);

				// Create the comment and set the text+author
				Comment comment = drawing.createCellComment(anchor);


				RichTextString stringComment = factory.createRichTextString("Iteration "+commentText + ": " + value);

				comment.setString(stringComment);
				cell.setCellComment(comment);
			}
			else{
				Comment comment = cell.getCellComment();
				RichTextString rtString =  comment.getString();
				String commentNormalString = rtString.toString();
				commentNormalString = "Iteration "+commentText + ": " + value + "\n" +commentNormalString;
				RichTextString stringComment = factory.createRichTextString(commentNormalString);
				comment.setString(stringComment);
				cell.setCellComment(comment);
			}


			if(value != "pass"){

				if(cell.getCellComment().getString().toString().contains("pass")){
					cell.setCellValue("Pass/Fail");
					cell.setCellStyle(styleOrange);
				}
				else{
					cell.setCellValue(value);
					cell.setCellStyle(styleRed);
				}
			}
			else{
				if(cell.getCellComment().getString().toString().contains("fail")){
					cell.setCellValue("pass/fail");
					cell.setCellStyle(styleOrange);
				}
				else{
					cell.setCellValue(value);
					cell.setCellStyle(styleGreen);
				}
			}



			fileIn.close();
			FileOutputStream fileOut = new FileOutputStream(this.path);
			workb.write(fileOut);
	
			workb.close();
		}
		finally {
			if (fileIn != null)
				fileIn.close();
		}

	}
	public void setValueCommentExecute(int rowNum, int colNum, String value, String commentText) throws IOException{

		FileInputStream fileIn = null;

		try{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);

			CreationHelper factory = workb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();

			
			HSSFRow row = sheet.getRow(rowNum);
			HSSFCell cell = row.getCell(colNum);

			if(cell == null){
				cell = row.createCell(colNum);
			}

			cell.setCellType(CellType.STRING);

			if(cell.getCellComment() == null){

				ClientAnchor anchor = factory.createClientAnchor();
				anchor.setCol1(cell.getColumnIndex());
				anchor.setCol2(cell.getColumnIndex()+3);
				anchor.setRow1(row.getRowNum());
				anchor.setRow2(row.getRowNum()+4);

				// Create the comment and set the text+author
				Comment comment = drawing.createCellComment(anchor);


				RichTextString stringComment = factory.createRichTextString("Iteration "+commentText + ": " + value);

				comment.setString(stringComment);
				cell.setCellComment(comment);
			}
			else{
				Comment comment = cell.getCellComment();
				RichTextString rtString =  comment.getString();
				String commentNormalString = rtString.toString();
				commentNormalString = "Iteration "+commentText + ": " + value + "\n" +commentNormalString;
				RichTextString stringComment = factory.createRichTextString(commentNormalString);
				comment.setString(stringComment);
				cell.setCellComment(comment);
			}

			if(value != "pass"){
				cell.setCellValue("Yes");
			}
			else{
				if(cell.getCellComment().getString().toString().contains("fail")){
					cell.setCellValue("Yes");
				}
				else{
					cell.setCellValue("No");
				}
			}

			fileIn.close();
			FileOutputStream fileOut = new FileOutputStream(this.path);
			workb.write(fileOut);
		
			workb.close();
		}
		finally {
			if (fileIn != null)
				fileIn.close();
		}

	}


	public void deleteColumn(int colNum, boolean RemoveOnlyComments) throws IOException {
		FileInputStream fileIn = null;
	
		try{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);


			Iterator <Row> rows     = sheet.rowIterator();
			if(RemoveOnlyComments){
				while (rows.hasNext()) {
					Row row = rows.next();
					Cell cell = row.getCell(colNum);
					if(cell != null){
						cell.removeCellComment();
					}
				}
			}
			else{
				while (rows.hasNext()) {
					Row row = rows.next();
					Cell cell = row.getCell(colNum);
					if(cell != null){
						cell.removeCellComment();
						row.removeCell(cell);
					}
				}
			}

		
			fileIn.close();
			FileOutputStream fileOut = new FileOutputStream(this.path);
			workb.write(fileOut);

			workb.close();
		}
		finally {
			if (fileIn != null)
				fileIn.close();
		}

	}


	public void AddColumnNum(int rowNum, int colNum , String ColumnName) throws IOException {
		

		FileInputStream fileIn = null;
		

		try{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);


		
			HSSFRow row = sheet.getRow(rowNum);
			HSSFCell cell = row.createCell(colNum);


		
			cell.setCellType(CellType.STRING);
			cell.setCellValue(ColumnName);
			cell.setCellStyle(row.getCell(cell.getColumnIndex() - 1).getCellStyle());

			
			fileIn.close();
			FileOutputStream fileOut = new FileOutputStream(this.path);
			workb.write(fileOut);
			
			workb.close();
		}
		finally {
			if (fileIn != null)
				fileIn.close();
		}

	}

	public List<Integer> getRunNumsMixed(String testEnv, String suiteName ) throws IOException, FrameworkException {
		// Execute -- yes , true , 1 , -1
		// TestEnv  --- string
		//suite Name 
	
		int rowNum = 0;
		List<Integer> runList = new ArrayList<Integer>();
		FileInputStream fileIn = null;
		DataFormatter formatter = new DataFormatter();


		Integer executeColumn = getColumnNum("Execute" , 0);
		Integer TestEnv = getColumnNum("TestEnv" , 0);
		Integer suiteColumn = getColumnNum(suiteName , 0);


		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);
			
			Iterator <Row> rows     = sheet.rowIterator();
			while (rows.hasNext()) {
				Row row = rows.next();
				Cell cell = row.getCell(executeColumn);
				
				if(!(formatter.formatCellValue(cell).equalsIgnoreCase("no") 
						|| formatter.formatCellValue(cell).equalsIgnoreCase("false")
						||formatter.formatCellValue(cell).equalsIgnoreCase("0")
						||formatter.formatCellValue(cell).equalsIgnoreCase(""))){
					Cell cellEnv = row.getCell(TestEnv);
					if(formatter.formatCellValue(cellEnv).equalsIgnoreCase(testEnv)) {
						Cell cellSuite = row.getCell(suiteColumn);
						if(!(formatter.formatCellValue(cellSuite).equalsIgnoreCase("no") 
								|| formatter.formatCellValue(cellSuite).equalsIgnoreCase("false")
								||formatter.formatCellValue(cellSuite).equalsIgnoreCase("0")
								||formatter.formatCellValue(cellSuite).equalsIgnoreCase(""))){
							rowNum = row.getRowNum();
							runList.add(rowNum);
						}
					}

				}
			}
			workb.close();

		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return runList;
	}

	public ArrayDeque<HashMap<String, String>> getRunTestsMixed(List<Integer> rowNums) throws IOException, FrameworkException{
	
		
		ArrayDeque<HashMap<String, String>> runList = new ArrayDeque<HashMap<String, String>>(400);
		FileInputStream fileIn = null;
		DataFormatter formatter = new DataFormatter();


		Integer TestName = getColumnNum("TestName" , 0);
		Integer Iteration_Mode = getColumnNum("Iteration_Mode" , 0);
		Integer Start_Iteration= getColumnNum("Start_Iteration" , 0);
		Integer End_Iteration= getColumnNum("End_Iteration" , 0);
		Integer TestID= getColumnNum("TestID" , 0);
		Integer Application= getColumnNum("Application" , 0);
		Integer TestDescription= getColumnNum("TestDescription" , 0);
		Integer Locale_MT= getColumnNum("Locale_MT" , 0);
		Integer TestEnv= getColumnNum("TestEnv" , 0);
		Integer SingleBrowser= getColumnNum("Single_Browser" , 0);
		Integer Driver= getColumnNum("Driver" , 0);
		Integer DriverCreated= getColumnNum("DriverCreated" , 0);

		Integer JiraTestKey= getColumnNum("JiraTestKey" , 0);
		try
		{
			fileIn = new FileInputStream(this.path);
			POIFSFileSystem fs = new POIFSFileSystem(fileIn);
			HSSFWorkbook workb = new HSSFWorkbook(fs);
			HSSFSheet sheet = workb.getSheet(this.datasheetName);

			for (Integer rowCur : rowNums) {
				HashMap<String,String> list = new HashMap<String,String>();
				list.put("TestName", getValue(rowCur, TestName));
				
				list.put("Iteration_Mode", getValue(rowCur, Iteration_Mode));
				
				list.put("Start_Iteration",getValue(rowCur, Start_Iteration) );
				
				list.put("End_Iteration",getValue(rowCur, End_Iteration) );
				
				list.put("TestID", getValue(rowCur, TestID));
				
				list.put("Application",getValue(rowCur, Application) );
				
				list.put("TestDescription",getValue(rowCur, TestDescription) );
				
				list.put("Locale_MT", getValue(rowCur, Locale_MT));
				
				list.put("TestEnv", getValue(rowCur, TestEnv));
				
				list.put("Single_Browser",getValue(rowCur, SingleBrowser) );
				
				list.put("Driver", getValue(rowCur, Driver));
				
				list.put("DriverCreated",getValue(rowCur, DriverCreated) );

				list.put("JiraTestKey",getValue(rowCur, JiraTestKey));

				runList.add(list);
			}
			workb.close();

		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return runList;
	}

}