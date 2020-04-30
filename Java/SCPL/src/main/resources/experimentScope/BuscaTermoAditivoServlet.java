package br.cefetrj.sisgee.view.termoaditivo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.cefetrj.sisgee.control.AlunoServices;
import br.cefetrj.sisgee.control.TermoAditivoServices;
import br.cefetrj.sisgee.model.entity.Aluno;
import br.cefetrj.sisgee.model.entity.TermoAditivo;
import br.cefetrj.sisgee.model.entity.TermoEstagio;
import br.cefetrj.sisgee.view.utils.ServletUtils;
import br.cefetrj.sisgee.view.utils.ValidaUtils;
/**
 * Servlet responsável pela busca de informações de cada termo aditivo
 * @author Vinicius Paradellas
 * @since 1.1
 *
 */

@WebServlet("/BuscaTermoAditivoServlet")
public class BuscaTermoAditivoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

        /**
         * 
        * @param request um objeto HttpServletRequest que contém a solicitação feita pelo cliente do servlet.
        * @param response um objeto HttpServletResponse que contém a resposta que o servlet envia para o cliente
        * @throws ServletException se o pedido do service não puder ser tratado
        * @throws IOException se um erro de entrada ou saída for detectado quando o servlet manipula o pedido 
         */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Locale locale = ServletUtils.getLocale(request);
		ResourceBundle messages = ResourceBundle.getBundle("Messages", locale);
		
		String msg = null;
		String idAluno = request.getParameter("idAluno");
                String mat = request.getParameter("matricula");
                
		Integer id = null;
                System.out.println("Aqui >>> "+idAluno);
		if(Integer.parseInt(idAluno)!=-1){
		msg = ValidaUtils.validaObrigatorio("Aluno", idAluno);		
		if(msg.trim().isEmpty()) {
			msg = ValidaUtils.validaInteger("Aluno", idAluno);
			if(msg.trim().isEmpty()) {
				id = Integer.parseInt(idAluno);					
			}else {
				msg = messages.getString(msg);
			}
		} else {
                    msg = messages.getString(msg);
		}
		
                Aluno aluno = AlunoServices.buscarAluno(new Aluno(id));
		List<TermoEstagio> termoEstagios =  aluno.getTermoEstagios();
		
		//TODO consertar a lógica de mensagem vazia
		if(msg != "") {
			aluno = AlunoServices.buscarAluno(new Aluno(id));				
			termoEstagios = aluno.getTermoEstagios();			
		}
		
                if (termoEstagios != null) {
                  //request.setAttribute("termosAditivos",TermoAditivoServices.listarTermoAditivo());
                  if(aluno.getTermoEstagios().size() > 0){
                      request.setAttribute("termosAditivos", aluno.getTermoEstagios().get(aluno.getTermoEstagios().size()-1).getTermosAditivos());
                  }
                }
		
                request.setAttribute("listaTermoEstagio", aluno.getTermoEstagios());
                request.setAttribute("msg",msg);
                }
		request.getRequestDispatcher("/form_termo_aditivo.jsp").forward(request, response);

	}
}