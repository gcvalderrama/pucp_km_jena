package com.pucp;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.util.FileManager;
import java.io.*;
import java.util.Iterator;
import java.util.List;

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

import javax.swing.*;

public class Main {
    public static String RecetarioXS = "http://www.recetario.com/";
    static final String inputFileName  = "receta_base.owl";

    private static void PrintModel(Model model)
    {
        System.out.println("Printing Model========");
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource  subject   = stmt.getSubject();     // get the subject
            Property  predicate = stmt.getPredicate();   // get the predicate
            RDFNode   object    = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
    }
    private  static void PrintResource(Resource resource)
    {
        StmtIterator iter = resource.listProperties();
        while (iter.hasNext()) {
            System.out.println("    " + iter.nextStatement()
                    .getObject()
                    .toString());
        }
    }

    private static void RecetarioSample()
    {
        Model instances = ModelFactory.createDefaultModel();

        instances.read("file:receta_base.owl");

        Model rule_model = ModelFactory.createDefaultModel();
        Resource configuration = rule_model.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleSet, "rulesa.rdf");
        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
        InfModel inf = ModelFactory.createInfModel(reasoner, instances);


        Resource dietetica = inf.getResource("http://www.recetario.com/dietetica");

        StmtIterator iter = inf.listStatements(dietetica, null, (RDFNode) null );
        //StmtIterator iter = inf.listStatements();
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();
            Resource  subject   = stmt.getSubject();
            Property  predicate = stmt.getPredicate();
            RDFNode   object    = stmt.getObject();

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
    }
    private static  void RecetarioSample_ttl()
    {
        Model instances = ModelFactory.createDefaultModel();
        instances.read("file:recetas_turtle.ttl");
        //instances.read("file:receta_base.owl");

        Model rule_model = ModelFactory.createDefaultModel();
        Resource configuration = rule_model.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleSet, "rules.rdf");
        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
        InfModel inf = ModelFactory.createInfModel(reasoner, instances);


        Resource dietetica = inf.getResource("http://www.recetario.com/#dietetica");

        StmtIterator iter = inf.listStatements(dietetica, null, (RDFNode) null );
        //StmtIterator iter = inf.listStatements();
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();
            Resource  subject   = stmt.getSubject();
            Property  predicate = stmt.getPredicate();
            RDFNode   object    = stmt.getObject();

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
    }

    private static void Recetario()
    {
        // write your code here

        //OntModel ont_model =  ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        Model raw_model = FileManager.get().loadModel("receta_base.owl");

        //PrintModel(raw_model);

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


        Model rule_model = ModelFactory.createDefaultModel();
        Resource configuration = rule_model.createResource();
        List<Rule> rules =  Rule.rulesFromURL("rulesa.rdf");
        //String rules = "[rule1: (?a eg:p ?b) (?b eg:p ?c) -> (?a eg:p ?c)]";
        Reasoner reasoner = new GenericRuleReasoner(rules);



        reasoner.setDerivationLogging(true);
        InfModel inf = ModelFactory.createInfModel(reasoner, raw_model);

        PrintModel(inf.getDeductionsModel());

        /*
        PrintWriter out = new PrintWriter(System.out);
        Resource receta = inf.getResource("http://www.recetario.com/AltasCalorias");

        for (StmtIterator i = inf.listStatements( receta, null, (Resource)null) ; i.hasNext(); ) {
            Statement s = i.nextStatement();
            System.out.println("Statement is " + s);
            for (Iterator id = inf.getDerivation(s); id.hasNext(); ) {
                Derivation deriv = (Derivation) id.next();
                deriv.printTrace(out, true);
            }
        }
        out.flush();

        */
        /*
        Resource resource =  inf_model.getResource("http://www.recetario.com/AltasCalorias");
        PrintResource(resource);

        Model rule_model = ModelFactory.createDefaultModel();
        Resource configuration = rule_model.createResource();
        configuration.addProperty(ReasonerVocabulary.PROPruleSet, "rulesa.rdf");
        Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
        InfModel reasoner_model = ModelFactory.createInfModel(reasoner, inf_model);


        Model deduction = reasoner_model.getDeductionsModel();
        PrintModel(deduction);
        */


        /*
        for (StmtIterator i = reasoner_model.listStatements((Resource)null , null,resource); i.hasNext(); ) {
            Statement stmt = i.nextStatement();
            System.out.println(" rs -- " + PrintUtil.print(stmt));
        }
        */
      /*
        PrintUtil.registerPrefix("recetario", "http://www.recetario.com/#");
        */
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

    }
    public static void main(String[] args) {
        RecetarioSample();
    }
}
