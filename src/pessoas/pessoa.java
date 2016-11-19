/**
 * Desenvolvido por: Amanda, Anderson, Jeronimo e Matheus,
 * Para a cadeira de BD II da Unisinos
 */
package pessoas;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;
import com.sleepycat.dbxml.XmlQueryContext;

/**
* Pessoas é um programa Berkeley DB XML muito simples
* Que executa uma consulta e manipula resultados.
* Demonstra inicialização, criação de contêiner,
* Inserção de documentos, criação e execução de consultas,
* Uso de uma variável em uma consulta e contexto, e
* Tratamento de resultados
 */
class pessoa{
	// Esta função é usada para assegurar que os bancos de dados sejam fechados corretamente, mesmo com exceções
    private static void cleanup(XmlManager mgr, XmlContainer cont) {
	try {
	    if (cont != null)
		cont.delete();
	    if (mgr != null)
		mgr.delete();
	} catch (Exception e) {
		// ignorar exceções na limpeza
	}
    }

    public static void main(String args[])
	throws Throwable {
    	
    // Este programa usa um recipiente nomeado, que aparecerá no disco
	String containerName = "pessoas.dbxml";
	//String que contém o XML
	String content = "<pessoas>"
					+"	<pessoa>"
					+"		<nome>Amanda</nome>"
					+"		<semestre>7</semestre>"
					+"		<curso>Ciência da Computação</curso>"
					+"	</pessoa>"
					+"	<pessoa>"
					+"		<nome>Anderson</nome>"
					+"		<semestre>7</semestre>"
					+"		<curso>Ciência da Computação</curso>"
					+"	</pessoa>"
					+"	<pessoa>"
					+"		<nome>Jeronimo</nome>"
					+"		<semestre>5</semestre>"
					+"		<curso>Análise e Desenvolvimento de Sistemas</curso>"
					+"	</pessoa>"
					+"</pessoas>"
			+ "";
	String docName = "pessoa";
	// Observe que a consulta usa uma variável, que deve ser definida no contexto da consulta
	String queryString =
	    "collection('pessoas.dbxml')/pessoas/pessoa[curso=$curso]";

	// declarar estes aqui para limpeza
	XmlManager mgr = null;
	XmlContainer cont = null;
	try {
		// Todos os programas BDB XML requerem uma instância XmlManager
	    mgr = new XmlManager();

	    // Como o contêiner existirá no disco, remova-o primeiro se existir
	    if (mgr.existsContainer(containerName) != 0)
		mgr.removeContainer(containerName);

	    // Agora é seguro criar o contêiner
	    cont = mgr.createContainer(containerName);

	    cont.putDocument(docName, content);

	    // A query pessoas requer um XmlQueryContext
	    XmlQueryContext qc = mgr.createQueryContext();
	    
	    // Adiciona uma variável ao contexto da consulta, usada pela consulta
	    qc.setVariableValue("curso", new XmlValue("Ciência da Computação"));
	    
	    // Nota: essas duas chamadas poderiam ser substituídas por um atalho mgr.query
	    XmlQueryExpression expr = mgr.prepare(queryString, qc);
	    XmlResults res = expr.execute(qc);
	
	    // Now, get the document
	    XmlDocument doc = cont.getDocument(docName);
	    String name = doc.getName();
	    String docContent = doc.getContentAsString();
	    
	    // print it
	    System.out.println("Nome do Documento: " + name + "\nXML: " +
			       docContent);
	  
	    // Observe o uso de XmlQueryExpression :: getQuery () e XmlResults :: size ()
	    // Resulta quantidade de resultados encontrados na consulta
	    System.out.println("\n A consulta, '" + expr.getQuery() +
			       "' retornou " + res.size() + " resultdo(s)");
	    
	    // Processar resultados - basta imprimi-los
	    XmlValue value = new XmlValue();
	    System.out.print("\nResultado: \n\n");
	    while ((value = res.next()) != null) {
		System.out.println("" + value.asString());
	    }

	    // Explicitamente excluir outros objetos para liberar recursos
	    res.delete();
	    expr.delete();
	} catch (XmlException xe) {
	    System.err.println("XmlException: " + 
			       xe.getMessage());
	    throw xe;
	}
	finally {
	    cleanup(mgr, cont);
	}
    }
}


