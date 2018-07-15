package com.rain.demo;

import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.dynamicreports.examples.Templates;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.ExporterBuilders;
import net.sf.dynamicreports.jasper.builder.export.JasperHtmlExporterBuilder;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;

public class DynamicreportsDemo {
	
	public static void main(String[] args) {
		//pdf docx html xml
		exportFile("pdf");
	}
	

	public static void exportFile(String reportType) {//��������
		
		//���λ��
		String dir = "D:/";
		
		int info = 0;
		int warning = 0;
		int error = 0;
		List<String> typeList = new ArrayList<String>();//������͵ļ���
		List<String> checkerList = new ArrayList<String>();//��ż�����ļ���
		List<Map<String, String>> detailInfoList = new ArrayList<Map<String, String>>();//��ż����ϸ��Ϣ�ļ���
		FileOutputStream fileOutputStream = null;
		try {
			//�������صȼ�����
			Map<String, Integer> seriousMap = new HashMap<String, Integer>();
			seriousMap.put("info", info);
			seriousMap.put("warning", warning);
			seriousMap.put("error", error);
			JRDataSource seriousDataSource = createDataSource(seriousMap);
			//����ȱ�����͸���
			Map<String, Integer> typeMap = new HashMap<String, Integer>();
			if (!typeList.isEmpty()) {
				for (String string : typeList) {
					if (typeMap.containsKey(string)) {
						typeMap.put(string, typeMap.get(string).intValue() + 1);
					} else {
						typeMap.put(string, new Integer(1));
					}
				}
			}
			//���ܼ��������
			Map<String, Integer> checkerMap = new HashMap<String, Integer>();
			if (!checkerList.isEmpty()) {
				for (String string : checkerList) {
					if (checkerMap.containsKey(string)) {
						checkerMap.put(string, checkerMap.get(string).intValue() + 1);
					} else {
						checkerMap.put(string, new Integer(1));
					}
				}
			}
			//������ʽ
			StyleBuilder boldStl = stl.style().bold();//����
			StyleBuilder titleStyle = stl.style(boldStl).setFontName("FreeUniversalSIMFang")
					.setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE).setFontSize(30);
			StyleBuilder columnTitleStyle = stl.style(boldStl).setFontName("FreeUniversalSIMFang").
					setFontSize(15).setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE).setBorder(stl.pen1Point())
					.setBackgroundColor(Color.LIGHT_GRAY);
			StyleBuilder columnDataStyle = stl.style(boldStl).setFontName("FreeUniversalSIMFang")
					.setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE).setFontSize(12).setPadding(5);
			StyleBuilder dateStyle = stl.style(boldStl).setFontName("FreeUniversalSIMFang")
					.setTextAlignment(HorizontalTextAlignment.RIGHT, VerticalTextAlignment.MIDDLE).setFontSize(15);
			
			JasperReportBuilder report = report();
			TextColumnBuilder<String> itemColumn = col.column("ȱ������", "item", type.stringType());
			TextColumnBuilder<Integer> quantityColumn = col.column("����", "quantity", type.integerType());
			//���صȼ�ͼ��
			Bar3DChartBuilder seriousChart = cht.bar3DChart().setDataSource(seriousDataSource).setShowValues(true)
					.setTitle("���صȼ�����").setCategory(itemColumn).series(cht.serie(quantityColumn));
			JRDataSource defectTypeDataSource = createDataSource(typeMap);
			JRDataSource defectTableDataSource = createDataSource(typeMap);
			//ȱ������ͼ��
			Bar3DChartBuilder typeChart = cht.bar3DChart().setDataSource(defectTypeDataSource).setShowValues(true)
					.setTitle("ȱ�ݷ������").setCategory(itemColumn).series(cht.serie(quantityColumn))
					.setCategoryAxisFormat(cht.axisFormat().setLabel("����"));
			TextColumnBuilder<String> checkerColumn = col.column("�����", "item", type.stringType());
			TextColumnBuilder<Integer> checkerQuantityColumn = col.column("����", "quantity", type.integerType());
			TextColumnBuilder<String> checkersColumn = col.column("�����", "checker", type.stringType()).setStyle(columnDataStyle).setWidth(200);
			TextColumnBuilder<String> fileNameColumn = col.column("�ļ���", "fileName", type.stringType()).setStyle(columnDataStyle).setWidth(150);
			TextColumnBuilder<String> fullPathColumn = col.column("�ļ�λ��", "fullPath", type.stringType()).setStyle(columnDataStyle).setWidth(200);
			TextColumnBuilder<String> messageColumn = col.column("��Ϣ", "message", type.stringType()).setStyle(columnDataStyle).setWidth(300);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String currentDate = simpleDateFormat.format(date);
			report.title(//shows report title
					cmp.horizontalList().add(cmp.text("C++�����鱨��").setStyle(titleStyle)).newRow()
							.add(cmp.text(currentDate).setStyle(dateStyle)).newRow()
							.add(cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10)).newRow()
							.add(cmp.horizontalList(seriousChart, typeChart)).newRow(10));
			//���صȼ��ӱ���
			JasperReportBuilder seriousSubreport = report();
			seriousSubreport.title(cmp.text("ȱ�ݷ����").setStyle(dateStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))
					.setColumnTitleStyle(columnTitleStyle).highlightDetailEvenRows().columns(itemColumn, quantityColumn)
					.setDataSource(defectTableDataSource);
			//������ӱ���
			JRDataSource checkerDataSource = createDataSource(checkerMap);
			JasperReportBuilder checkerSubreport = report();
			checkerSubreport.setColumnTitleStyle(columnTitleStyle).highlightDetailEvenRows();
			checkerSubreport.title(cmp.horizontalList().newRow(10)
					.add(cmp.text("�������").setStyle(dateStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)));
			checkerSubreport.columns(checkerColumn, checkerQuantityColumn).setDataSource(checkerDataSource);
			
			
			//ȱ����ϸ��Ϣ�ӱ���
			JasperReportBuilder detailInfoSubreport = report();
			detailInfoSubreport.setTemplate(Templates.reportTemplate).setColumnTitleStyle(columnTitleStyle)
					.highlightDetailEvenRows();
			detailInfoSubreport.title(cmp.horizontalList().newRow(10)
					.add(cmp.text("ȱ���б���ϸ��Ϣ").setStyle(dateStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)));
			detailInfoSubreport.columns(checkersColumn, fileNameColumn, fullPathColumn, messageColumn)
					.setDataSource(createDataSource(detailInfoList));
			report.setTemplate(Templates.reportTemplate).detail(cmp.subreport(seriousSubreport)).addNoData(cmp.gap(300, 30))
					.detail(cmp.subreport(checkerSubreport)).detail(cmp.subreport(detailInfoSubreport))
					.pageFooter(Templates.footerComponent).setDataSource(createDataSource());
			//					report.show();//����չʾ
			String reportDir = dir + File.separator;
			switch (reportType) {
				case "pdf":
					reportDir += "report.pdf";
					fileOutputStream = new FileOutputStream(reportDir);
					report.toPdf(fileOutputStream);
					break;
				case "html":
					File file = new File(reportDir + "report.html");
					JasperHtmlExporterBuilder htmlExporter = new ExporterBuilders().htmlExporter(file)
							.setImagesDirName(reportDir + File.separator + "images").setOutputImagesToDir(true);
					//							reportName += ".html";
					//							fileOutputStream = new FileOutputStream(reportName); 
					//				            report.toHtml(fileOutputStream);
					report.toHtml(htmlExporter);
					break;
				case "xml":
					reportDir += "report.xml";
					fileOutputStream = new FileOutputStream(reportDir);
					report.toXml(fileOutputStream);
					break;
				case "docx":
					reportDir += "report.docx";
					fileOutputStream = new FileOutputStream(reportDir);
					report.toDocx(fileOutputStream);
					break;
				default:
					throw new RuntimeException("��֧�ֵ��ļ���ʽ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} 
	private static JRDataSource createDataSource() {
		return new JREmptyDataSource();
	}

	//�������صȼ���ȱ����������   
	private static JRDataSource createDataSource(Map<String, Integer> dataMap) {
		DRDataSource dataSource = new DRDataSource("item", "quantity");
		if (null != dataMap) {
			for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
				dataSource.add(entry.getKey(), entry.getValue());
			}
		}
		return dataSource;
	}

	//����ȱ���б���ϸ��Ϣ����
	private static JRDataSource createDataSource(List<Map<String, String>> detailInfoList) {
		
		Map<String, String> map = new HashMap<>();
		map.put("checker", "asdf");
		map.put("fileName", "asdfasd");
		map.put("fullPath", "asdfaasdfa�ĵ�  sd");
		map.put("message", "asdfas��˹�ٷ�d");
		List<Map<String, String>> list = new ArrayList<>();
		list.add(map);
		detailInfoList = list;
		
		
		DRDataSource dataSource = null;
		if (null != detailInfoList && detailInfoList.size() > 0) {
			Map<String, String> keymap = detailInfoList.get(0);
			Set<String> keySet = keymap.keySet();
			String[] array = keySet.toArray(new String[keySet.size()]);
			dataSource = new DRDataSource(array);
			for (Map<String, String> tempMap : detailInfoList) {
				String data[] = new String[keySet.size()];
				int i = 0;
				for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					data[i] = tempMap.get(key);
					System.out.println(data[i]);
					i++;
				}
				dataSource.add(data);
			}
		}
		return dataSource;
	}
}
