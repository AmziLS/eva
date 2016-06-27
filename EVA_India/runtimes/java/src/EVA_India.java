class EVA_India {
	
	public static void main(String[] args) throws Exception {
		EVA_Case evacase;
		EVA eva = null;
		System.out.println("EVA India");
		evacase = new EVA_Case("1", "date(2010,3,3)", "date(2010,3,5)", "all", "female", "no comment" );
		evacase.add_vaccination("BCG", "date(2010,3,4)");
		evacase.add_vaccination("OPV", "date(2010,3,4)");
		evacase.add_vaccination("HepatitisB", "date(2010,3,4)");
		try{
			eva = new EVA();
			eva.add_case(evacase);
			eva.analyze();
			eva.display();
		}
		finally {
			if (eva != null) eva.close();
		}
		
		
	}
}