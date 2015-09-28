package com.pucp;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.util.FileManager;
import java.io.*;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

public class Main {
    public static String RecetarioXS = "http://www.recetario.com/";
    static final String inputFileName  = "receta_base.owl";

    public static void main(String[] args) {
	// write your code here

        //OntModel ont_model =  ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        Model raw_model = FileManager.get().loadModel("receta_base.owl");

        InfModel inf_model =  ModelFactory.createRDFSModel(raw_model);
      //  FileManager.get().readModel( ont_model, inputFileName );


        ValidityReport validaty = inf_model.validate();
        if (!validaty.isValid())
        {
            System.out.println("Conflicts");
            for (Iterator i = validaty.getReports(); i.hasNext(); ) {
                System.out.println(" - " + i.next());
            }
        }


        Resource resource =  inf_model.getResource("http://www.recetario.com/Vegetariana");

        StmtIterator iter = resource.listProperties();
        while (iter.hasNext()) {
            System.out.println("    " + iter.nextStatement()
                    .getObject()
                    .toString());
        }



        Model rule_model = ModelFactory.createDefaultModel();
        Resource configuration = rule_model.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleSet, "rules.rdf");
        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);

        InfModel rule_inf_model = ModelFactory.createInfModel(reasoner, inf_model);


        PrintUtil.registerPrefix("recetario", "http://www.recetario.com/#");
        for (StmtIterator i = rule_inf_model.listStatements(resource, null,(Resource)null); i.hasNext(); ) {
             Statement stmt = i.nextStatement();
             System.out.println(" - " + PrintUtil.print(stmt));
        }

        //https://jena.apache.org/tutorials/rdf_api.html
        //https://github.com/gcvalderrama/pucp_km_jena/blob/master/src/main/java/com/pucp/Main.java
        //https://jena.apache.org/tutorials/rdf_api.html




        //  Model model = ModelFactory.createDefaultModel();

        // InputStream in = FileManager.get().open( inputFileName );

        //  if (in == null) {
        //      throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        //  }

        // read the RDF/XML file
        // model.read(new InputStreamReader(in), "");


        // write it to standard out
        //ont_model.write(System.out);
        /*
        String prefix = "prefix recetas: <" + RecetarioXS + ">\n" +
                "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                "prefix owl: <" + OWL.getURI() + ">\n";

        System.out.println(prefix);
        String q =  prefix +
                "select ?result where { ?result rdfs:subClassOf ?restriccion . \n" +
                " ?restriccion owl:onProperty recetas:tiene_ingrediente ; owl:someValuesFrom recetas:Tallarin }" ;

        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, ont_model);
        try {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out( results, ont_model );
        }
        finally {
            qexec.close();
        }
        */

        System.out.println("hola mundo");
    }
}
