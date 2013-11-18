import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
//import java.util.ArrayList<E>;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

public class Dictionary_dispute {
	
	public static void main(String args[]) {
		
		/*
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager
					.getConnection(
							"jdbc:mysql://localhost/sample",
							"root", "");
			Statement stmt = con.createStatement();
			stmt.executeUpdate("delete from dispute_dictionary");
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the connection
			try {
				con.close();
			} catch (Exception e) {
				}
		}*/
		
		// variables declaration
		double sim = 0.0;
		SynsetType type = SynsetType.VERB;
		String seed = "disbelieve";
		
		ArrayList<String> compare = new ArrayList<String>();
		compare.add(seed);
		
		System.setProperty("wordnet.database.dir","C:/Program Files (x86)/WordNet/2.1/dict");
		WordNetDatabase database = WordNetDatabase.getFileInstance();

		for(int s=0; s<3; s++){
			compare = calculate_synset(seed, compare,  type, 0);
		}
		
		calculate_troponyms(compare);
		
		Synset[] synsets_hypernyms = database.getSynsets("disbelieve", type);
		ArrayList<String> word_hypernyms = new ArrayList<String>();
		for(int i=0; i<synsets_hypernyms.length; i++){
			
			int length=0;
			VerbSynset vbSynset_1 = (VerbSynset) synsets_hypernyms[i];
			word_hypernyms = new ArrayList<String>();
			word_hypernyms = calculate_lcs_hypernym(vbSynset_1,  word_hypernyms, 1); // Get hypernyms of 1st word	
		}
		for(int i=0; i<word_hypernyms.size(); i++){
			
			if(! seed.equals(word_hypernyms.get(i))){
				//System.out.println(word_hypernyms.get(i));
				System.out.println();
				sim = cal_prob(seed, word_hypernyms.get(i), type);
				//insert_db(seed, word_hypernyms.get(i), sim, type, "Hypernym");
				System.out.println("Hypernym Similarity("+seed+","+word_hypernyms.get(i)+")"+sim);
			}
		}
		//System.out.println("Similarity from main:"+sim);
		
		
		type = SynsetType.NOUN;
		seed = "disbeliever";		
		
		ArrayList<String> compare_noun = new ArrayList<String>();
		compare_noun.add(seed);
		
		for(int s=0; s<1; s++){
			compare_noun = calculate_synset( seed, compare_noun,  type, s);
			//insert_db(seed, word_hypernyms.get(i), sim, type);
		}
		
		seed = "doubt";		
		
		compare_noun = new ArrayList<String>();
		compare_noun.add(seed);
		
		for(int s=0; s<1; s++){
			compare_noun = calculate_synset( seed, compare_noun,  type, s);
		}
		
		
	}
	
	/*
	public static void insert_db (String seed, String word, double sim, SynsetType type, String form){
		Connection con = null;
		try {
			String str_type = type.toString();
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager
					.getConnection(
							"jdbc:mysql://localhost/sample",
							"root", "");
			Statement stmt = con.createStatement();
			
			System.out.println("Seed:"+seed+ ", word:"+word+ ", Sim:"+sim);
			
			stmt.executeUpdate("INSERT INTO dispute_dictionary (seed, word, sim, type, form) VALUES ('"
					+ seed
					+ "', '"
					+ word
					+ "', '"
					+ sim
					+ "', '"
					+ str_type
					+ "', '"
					+ form
					+ "')");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the connection
			try {
				con.close();
			} catch (Exception e) {
				}
		}
	}
	*/
	public static void calculate_derivationally_related(ArrayList<String> seed_list)
	{
		double p = 0.0;
		String seed = "doubt";
		
		SynsetType type1 = SynsetType.ADJECTIVE;
		SynsetType type2 = SynsetType.NOUN;
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets =  database.getSynsets(seed);
		Synset synset;
		WordSense[] word_sense;
		
		for(int i =0;i<synsets.length;i++)
		{
			synset = synsets[i];
			word_sense = synset.getDerivationallyRelatedForms(seed);
			for(int j = 0; j<word_sense.length;j++)
			{
				//String word_forms[] = word_sense[j].getSynset().getWordForms();
				String word_forms[] = word_sense[j].getSynset().getWordForms();
				//System.out.println("Derivationally related" + word_sense[j].getSynset().getWordForms());
				for(String str: word_forms)
				{
					System.out.println("derivationally related" + str);
					seed_list.add(str);
					


					for(int s=0; s<3; s++){
						seed_list = calculate_synset(str, seed_list,  type1, s);
					}
					
					for(int s=0; s<3; s++){
						seed_list = calculate_synset(seed, seed_list,  type2, s);
					}
					
					System.out.println();
					System.out.println("=========================================");
					System.out.println();
						

				}
			}
		}
			
			
	}
	
	public static ArrayList<String> calculate_synset(String seed, ArrayList<String> compare, SynsetType type, int counter){
		
		double p = 0.0;
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		Synset[] synsets = database.getSynsets(compare.get(counter), type);
		
		for (int i = 0; i < synsets.length; i++) {
			
			String[] wsynsets = synsets[i].getWordForms();
			
			for (int j = 0; j < wsynsets.length; j++) {
				
				if (compare.contains(wsynsets[j]) ) {
					continue;
				} else {
					if (seed.equals(wsynsets[j])) {
						p = 1.0; // Similarity between word and itself is 1
					}
					p = cal_prob(seed,wsynsets[j], type);
					//insert_db(seed, wsynsets[j], p, type);
				}
				System.out.println();
				System.out.println("Synset Similarity("+seed+","+wsynsets[j]+")"+p);
				//insert_db(seed, wsynsets[j], p, type, "Synset");
				compare.add(wsynsets[j]);
			}
			// System.out.print(wsynsets[j]+",");
		}
		
		return compare;
	}
	
	public static void calculate_troponyms(ArrayList<String> seed_list)
	{
		double p = 0.0;
		String seed = "doubt";
		SynsetType type = SynsetType.VERB;
		VerbSynset verbSyn;
		VerbSynset[] troponyms;
		String troponym_def="";
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(seed, type);

		for(int t=0; t<3;t++)
		{
			for(int i=0; i<synsets.length;i++)
			{
				verbSyn = (VerbSynset)(synsets[i]);
				troponyms= verbSyn.getTroponyms();
				for(int j=0;j<troponyms.length;j++)
				{
					String[] troponym_array=troponyms[j].getWordForms();
					troponym_def=troponyms[j].getDefinition();
					for(String s: troponym_array)
					{
						if (seed_list.contains(s) ) {
							continue;
						} else {
							if (seed.equals(s)) {
								p = 1.0; // Similarity between word and itself is 1
							}
							p = cal_prob(seed, s, type);
							System.out.println();
							//System.out.println("=========================================");
							//System.out.println();
							System.out.println("Troponym Similarity("+seed+","+s+")"+p);
							//insert_db(seed, s, p, type, "Troponym");
						}

					} 
				}
			}
		}

	}
	
	
	
	
	public static double generate_hypernym(String word, SynsetType type)
	{
		VerbSynset verbSyn;
		VerbSynset[] hypernyms;
		String hypernym_def="";
		double sim = 0.0;
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		Synset[] synsets = database.getSynsets(word, type);
		
		for(int i=0; i<synsets.length;i++)
		{
			verbSyn = (VerbSynset)(synsets[i]);	
			hypernyms= verbSyn.getHypernyms();
			for(int j=0;j<hypernyms.length;j++)
			{
				String[] hypernym_array=hypernyms[j].getWordForms();
				System.out.println();
				hypernym_def=hypernyms[j].getDefinition();
				//System.out.println("Word:"+word+", Other:"+hypernym_array[j]);
				sim = cal_sim_1_hypernym(word, hypernym_array[j], type);
				//System.out.println("Similarity:"+sim);
			}
		}
		return sim;
	}
	
	public static double cal_prob(String root, String other,
			SynsetType type) {

		//System.out.println("Root:"+root+", Word:"+other);
		
		double root_den = 0.00;
		double root_num = 0.00;
		double rf_1 = 0.0;
		
		double other_den = 0.0;
		double other_num = 0.0;
		double rf_2 = 0.0;
		
		double p = 0.0;
		
		//int m = a;
		int case_1_match=0;
		
		String def;
		
		SynsetType typ = type;
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		Synset[] synsets_root = database.getSynsets(root, typ);
		
		if (synsets_root.length > 0) {
			for (int d = 0; d < synsets_root.length; d++) {
				root_den += synsets_root[d].getTagCount(root) == 0 ? 1 : synsets_root[d]
						.getTagCount(root);
			}
		}
		
		Synset[] synsets_other = database.getSynsets(other, typ);
		
		if (synsets_other.length > 0) {
			for (int d = 0; d < synsets_other.length; d++) {
				other_den += synsets_other[d].getTagCount(other) == 0 ? 1 : synsets_other[d]
						.getTagCount(other);
			}
		}
		
		for(int i=0; i<synsets_root.length; i++){
			
			for(int j=0; j<synsets_other.length; j++){
				
				if(synsets_root[i].getDefinition().equals(synsets_other[j].getDefinition()) ){
					//CASE - 1
					//System.out.println("Root:"+root+", Word:"+word);
					//System.out.println("Matched synset definition:"+synsets_root[i].getDefinition());
					case_1_match=1;
					
					root_num = synsets_root[i].getTagCount(root) == 0 ? 1 : synsets_root[i].getTagCount(root);
					
					if(synsets_root[i].getTagCount(root) == 0)
					{
					root_den=root_den+synsets_root.length;
					}
					
					//System.out.println("root_num:"+root_num);
					//System.out.println("root_den:"+root_den);
					rf_1 = root_num/ root_den;
					
					other_num = synsets_other[j].getTagCount(other) == 0 ? 1 : synsets_other[j].getTagCount(other);
					
					
					if(synsets_other[i].getTagCount(other) == 0)
					{
					other_den=other_den+synsets_other.length;
					}
					//System.out.println("other_num:"+other_num);
					//System.out.println("other_den:"+other_den);
					rf_2 = other_num/ other_den;
					System.out.println("=========================================");
					System.out.println("Case - 1 :: Root:"+root+", Word:"+other);
					p = rf_1 * rf_2;					
				}
				
			}
			
		}
		
		if(case_1_match == 0){
			//CASE - 2
			System.out.println("=========================================");
			System.out.println("Case - 2 :: Root:"+root+", Word:"+other);
			p = cal_sim_1_hypernym(root, other, typ);
		}
		
		return p;
	}

	
	public static double cal_sim_1_hypernym(String word, String other, SynsetType type){
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		//SynsetType type = SynsetType.VERB;
		
		double den_1 = 0.0;
		double den_2 = 0.0;
		double num_1 = 0.0;
		double num_2 = 0.0;
		double sim=0.0;
		
		Synset[] synsets = database.getSynsets(word, type);
		for (int d = 0; d < synsets.length; d++) {
			den_1 += synsets[d].getTagCount(word) == 0 ? 1 : synsets[d]
					.getTagCount(word);
		}
		
		Synset[] s_d_2 = database.getSynsets(other, type);
		for (int d = 0; d < s_d_2.length; d++) {
			den_2 += s_d_2[d].getTagCount(other) == 0 ? 1 : s_d_2[d]
					.getTagCount(other);
		}
		
		Synset[] synsets_1 = database.getSynsets(word, type);
		
		if( type.toString().equals("1") ){
				for(int i=0; i<synsets_1.length; i++){
				
				int length=0;
				NounSynset vbSynset_1 = (NounSynset) synsets_1[i];
				ArrayList<NounSynset> word_hypernyms = new ArrayList<NounSynset>();
				
				word_hypernyms = calculate_lcs_noun(vbSynset_1,  word_hypernyms, 1); // Get hypernyms of 1st word
				
				ArrayList<NounSynset> other_hypernyms = new ArrayList<NounSynset>();
				Synset[] synsets_2 = database.getSynsets(other, type);
				
				for(int j=0; j<synsets_2.length; j++){
					
					NounSynset vbSynset_2 = (NounSynset) synsets_2[j];
					other_hypernyms = new ArrayList<NounSynset>();
					
					other_hypernyms = calculate_lcs_noun(vbSynset_2, other_hypernyms, 1); // Get hypernyms of 2nd word
					
					if(word_hypernyms.toString().contains(vbSynset_2.toString()))
					{
						//System.out.println("Bala case - 1");
						length=find_common_ancestor2_noun(word_hypernyms, vbSynset_2);
						//System.out.println(length);
					}
					else if(other_hypernyms.toString().contains(vbSynset_1.toString())){
						//System.out.println("Bala case");
						length=find_common_ancestor2_noun(other_hypernyms, vbSynset_1);
					}
					else{
						length = find_common_ancestor(word_hypernyms, other_hypernyms);
					}
					//System.out.println("LCA between("+word + (i+1) +" and "+ other+ (j+1) +"):"+length);
					if(length != 0){
						//System.out.println("--------------------------------------------------------");
						//System.out.println("LCA between("+word + (i+1) +" and "+ other+ (j+1) +"):"+length);
						num_1 = vbSynset_1.getTagCount(word) == 0 ? 1 : vbSynset_1.getTagCount(word);
						num_2 = vbSynset_2.getTagCount(other) == 0 ? 1 : vbSynset_2.getTagCount(other);
						//System.out.println("Numerator of word-1:"+vbSynset_1.getTagCount(word));
						//System.out.println("Numerator of word-2:"+vbSynset_2.getTagCount(other));
						//System.out.println("Den of word-1:"+den_1);
						//System.out.println("Den of word-2:"+den_2);
						length = (int) Math.pow(length, length);
						if(vbSynset_1.getTagCount(word) == 0)
						{
							den_1=den_1+synsets_1.length;
						}
						
						if(vbSynset_2.getTagCount(word) == 0)
						{
							den_2=den_2+synsets_2.length;
						}
						
						length = length+1;
						//System.out.println(length);
						sim += (num_1*num_2)/(den_1*den_2*length);
						//System.out.println(sim);
					}
				}	
			}
		}
		if(type.toString().equals("2")){
			for(int i=0; i<synsets_1.length; i++){
				
				int length=0;
				VerbSynset vbSynset_1 = (VerbSynset) synsets_1[i];
				ArrayList<VerbSynset> word_hypernyms = new ArrayList<VerbSynset>();
				
				word_hypernyms = calculate_lcs(vbSynset_1,  word_hypernyms, 1, type); // Get hypernyms of 1st word
				
				ArrayList<VerbSynset> other_hypernyms = new ArrayList<VerbSynset>();
				Synset[] synsets_2 = database.getSynsets(other, type);
				
				for(int j=0; j<synsets_2.length; j++){
					
					VerbSynset vbSynset_2 = (VerbSynset) synsets_2[j];
					other_hypernyms = new ArrayList<VerbSynset>();
					
					other_hypernyms = calculate_lcs(vbSynset_2, other_hypernyms, 1, type); // Get hypernyms of 2nd word
					
					if(word_hypernyms.toString().contains(vbSynset_2.toString()))
					{
						//System.out.println("Bala case - 1");
						length=find_common_ancestor2(word_hypernyms, vbSynset_2);
						//System.out.println(length);
					}
					else if(other_hypernyms.toString().contains(vbSynset_1.toString())){
						//System.out.println("Bala case");
						length=find_common_ancestor2(other_hypernyms, vbSynset_1);
					}
					else{
						length = find_common_ancestor(word_hypernyms, other_hypernyms);
					}
					//System.out.println("LCA between("+word + (i+1) +" and "+ other+ (j+1) +"):"+length);
					if(length != 0){
						//System.out.println("--------------------------------------------------------");
						//System.out.println("LCA between("+word + (i+1) +" and "+ other+ (j+1) +"):"+length);
						num_1 = vbSynset_1.getTagCount(word) == 0 ? 1 : vbSynset_1.getTagCount(word);
						num_2 = vbSynset_2.getTagCount(other) == 0 ? 1 : vbSynset_2.getTagCount(other);
						//System.out.println("Numerator of word-1:"+vbSynset_1.getTagCount(word));
						//System.out.println("Numerator of word-2:"+vbSynset_2.getTagCount(other));
						//System.out.println("Den of word-1:"+den_1);
						//System.out.println("Den of word-2:"+den_2);
						
						
						if(vbSynset_2.getTagCount(other) == 0)
						{
							den_2=den_2+synsets_2.length;
						}
						
						if(vbSynset_1.getTagCount(word) == 0)
						{
							den_1=den_1+synsets_1.length;
						}
						length = (int) Math.pow(length, length);
						length = length+1;
						sim += (num_1*num_2)/(den_1*den_2*length);
						//System.out.println(sim);
					}
				}	
			}
		}
		
		
		
		//System.out.println("Similarity here:"+sim);
		return sim;
	}
	
	public static ArrayList<VerbSynset> calculate_lcs(VerbSynset word_1, ArrayList<VerbSynset> word_hypernyms, int length, SynsetType type){
		
		if(word_1 != null){
			VerbSynset[] hypernyms;
			String hypernym_def="";
			//SynsetType type = SynsetType.VERB;
			WordNetDatabase database = WordNetDatabase.getFileInstance();
			
			hypernyms = word_1.getHypernyms();
			
			for(VerbSynset verbSyn : hypernyms){
				//System.out.println("From calculate_lcs:"+verbSyn);
				word_hypernyms.add(verbSyn);
				calculate_lcs(verbSyn, word_hypernyms, length++, type);
				//return null;
				
			}
		}
		return word_hypernyms;
	}
	
	public static ArrayList calculate_lcs_noun(NounSynset word_1, ArrayList word_hypernyms, int length){
		
		if(word_1 != null ){
			NounSynset[] hypernyms;
			String hypernym_def="";
			SynsetType type = SynsetType.VERB;
			WordNetDatabase database = WordNetDatabase.getFileInstance();
			
			hypernyms = word_1.getHypernyms();
			
			for(NounSynset verbSyn : hypernyms){
				//System.out.println("From calculate_lcs:"+verbSyn);
				word_hypernyms.add(verbSyn);
				calculate_lcs_noun(verbSyn, word_hypernyms, length++);
				//return null;
				
			}
		}
		return word_hypernyms;
	}
	
	
	public static ArrayList<String> calculate_lcs_hypernym(VerbSynset word_1, ArrayList<String> word_hypernyms, int length){
		
		if(word_1 != null){
			VerbSynset[] hypernyms;
			String hypernym_def="";
			SynsetType type = SynsetType.VERB;
			WordNetDatabase database = WordNetDatabase.getFileInstance();
			
			hypernyms = word_1.getHypernyms();
			
			for(VerbSynset verbSyn : hypernyms){
				//System.out.println("From calculate_lcs:"+verbSyn);
				
				for(int i=0; i<verbSyn.getWordForms().length; i++)
				{
					//System.out.println(verbSyn.getWordForms()[i]);
					
					if(! word_hypernyms.contains(verbSyn.getWordForms()[i])){
						word_hypernyms.add(verbSyn.getWordForms()[i]);
					}
					
				}
				
				//word_hypernyms.add(verbSyn);
				calculate_lcs_hypernym(verbSyn, word_hypernyms, length++);
				
			}
		}
		return word_hypernyms;
	}
	
	
	
	public static int find_common_ancestor(ArrayList hypernym_1, ArrayList hypernym_2){
		
		for(int i=0; i<hypernym_1.size();i++ ){
			
			for(int j=0; j<hypernym_2.size();j++ ){
				//System.out.println("Hypernym set-2:"+hypernym_2.get(j));
				if(hypernym_1.get(i).equals(hypernym_2.get(j))){
					
					//System.out.println("Matched word is:"+hypernym_1.get(i));
					return (i+j+2);
				}
			}
		}
		return 0;
	}
	
	public static int find_common_ancestor2_noun(ArrayList hypernym_1, NounSynset vbSynset_2 ){

		for(int i=0; i<hypernym_1.size();i++ ){
			if(hypernym_1.get(i).equals(vbSynset_2)){
				return (i+1);
			}
			else{
				//do nothing
			}
		}
		return 0;
	}
	
	public static int find_common_ancestor2(ArrayList hypernym_1,VerbSynset vbSynset_2 ){

		for(int i=0; i<hypernym_1.size();i++ ){
			if(hypernym_1.get(i).equals(vbSynset_2)){
				return (i+1);
			}
			else{
				//do nothing
			}
		}
		return 0;
	}

}