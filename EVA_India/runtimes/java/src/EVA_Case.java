import java.util.ArrayList;
import java.util.List;

// The Amzi! & ARulesXL Java libraries
import amzi.arulesxl.*;
import amzi.ls.ARulesLogicServer;
import amzi.ls.ARulesLSException;

class EVA {
	private ARulesXL arxl;
	private ARulesLogicServer ls;
	private long array;
	private long row;
	private long result;
	
	public EVA_Case evacase;
	public String age;
	public List<EVA_History_Row> history;
	public List<EVA_Plan_Row> plan;
	
	EVA() throws Exception {
		arxl = new ARulesXL();
		
		try {
			arxl.openRules("eva_india.axl", "");
			ls = arxl.getLS();
		}
		catch (ARulesLSException ex) {
			System.out.println(ex.GetMsg());
		}
		
		history = new ArrayList<EVA_History_Row>();
		plan = new ArrayList<EVA_Plan_Row>();
	}
	
	void close() throws Exception {
		arxl.closeRules();
	}
	
	void add_case(EVA_Case ec){
		evacase = ec;
	}
	
	void display() {
		System.out.println("\nInput Case\n");
		evacase.display();
		System.out.println("\nOutput Analysis\n");
		System.out.println("Age = " + age);
		System.out.println("\nHistory\n");
		for (EVA_History_Row h : history) {
			h.display();
		}
		System.out.println("\nPlan\n");
		for (EVA_Plan_Row p : plan) {
			p.display();
		}
	}
	
	void analyze() throws Exception {
		arxl.clearVector("CommonRules", "data");
		arxl.addToVector("CommonRules", "data", "ID", evacase.id);
		arxl.addToVector("CommonRules", "data", "BirthDate", evacase.birthdate);
		arxl.addToVector("CommonRules", "data", "TestDate", evacase.visitdate);
		arxl.addToVector("CommonRules", "data", "Vaccines", evacase.vaccines);
		arxl.addToVector("CommonRules", "data", "Gender", evacase.gender);
		arxl.addToVector("CommonRules", "data", "Comment", evacase.comment);
		
		arxl.clearTable("CommonRules", "raw_vaccination");
		Integer i = new Integer(1);
		String is;
		for (Vaccination v : evacase.vaccinations) {
			is = i.toString();
			arxl.addToTable("CommonRules", "raw_vaccination", is, "Vaccination", v.vaccine);
			arxl.addToTable("CommonRules", "raw_vaccination", is, "VaccinationDate", v.date);
			i++;
		}
		
		age = arxl.queryRules("EVA", "FIND age");
		
		// Get the history array, and map it to a list of EVA_History_Rows
		result = ls.ExecStr("arxl_query('EVA', false, `FIND history`, ?x)");
		array = ls.GetArg(result, 4);
		while (array != 0){
			row = ls.GetHead(array);
			EVA_History_Row hrow = new EVA_History_Row();
			
			hrow.vaccination = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			hrow.dose = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			hrow.date_given = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			hrow.status = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			hrow.comment = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			hrow.age_given = ls.TermToStr( ls.GetHead(row), 500);
			
			history.add(hrow);
			array = ls.GetTail(array);
		}
		
		// Get the plan array, and map it to a list of EVA_Plan_Rows
		result = ls.ExecStr("arxl_query('EVA', false, `FIND plans`, ?x)");
		array = ls.GetArg(result, 4);
		while (array != 0){
			row = ls.GetHead(array);
			EVA_Plan_Row prow = new EVA_Plan_Row();
			
			prow.vaccination = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			prow.status = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			prow.dose = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			prow.earliest = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			prow.optimal_start = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			prow.optimal_end = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			prow.citation = ls.TermToStr( ls.GetHead(row), 500);
			row = ls.GetTail(row);
			prow.comment = ls.TermToStr( ls.GetHead(row), 500);
			
			plan.add(prow);
			array = ls.GetTail(array);
		}
	}
}

class EVA_History_Row {
	public String vaccination;
	public String dose;
	public String date_given;
	public String status;
	public String comment;
	public String age_given;
		
	void display() {
		System.out.println(vaccination+" "+dose+" "+date_given+" "+status+" "+comment+" "+age_given);
	}
}

class EVA_Plan_Row {
	public String vaccination;
	public String status;
	public String dose;
	public String earliest;
	public String optimal_start;
	public String optimal_end;
	public String citation;
	public String comment;
	
	void display() {
		System.out.println(vaccination+" "+status+" "+dose+" "+earliest+" "+optimal_start+" "+optimal_end+" "+citation+" "+comment);
	}
}

class Vaccination {
	public String vaccine;
	public String date;
	
	Vaccination(String v, String d) {
		vaccine = v;
		date = d;
	}
	
	void display() {
		System.out.println(vaccine + " " + date);
	}
}

class EVA_Case {
	public String id;
	public String birthdate;
	public String visitdate;
	public String vaccines;
	public String gender;
	public String comment;
	public List<Vaccination> vaccinations;
	
	EVA_Case(String i, String bd, String vd, String v, String g, String c) {
		id = i;
		birthdate = bd;
		visitdate = vd;
		vaccines = v;
		gender = g;
		comment = c;
		vaccinations = new ArrayList<Vaccination>();
	}
	
	void add_vaccination(String v, String d) {
		vaccinations.add(new Vaccination(v,d));
	}
	
	void display() {
		System.out.println("id:         " + id);
		System.out.println("birth date: " + birthdate);
		System.out.println("visit date: " + visitdate);
		System.out.println("vaccines:   " + vaccines);
		System.out.println("gender:     " + gender);
		System.out.println("comment:    " + comment);
		System.out.println("Vaccinations");
		for (Vaccination v : vaccinations) {
			v.display();
		}
		
	}
}
