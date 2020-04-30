package br.cefetrj.sisgee.view.termoaditivo;

import br.cefetrj.sisgee.control.AlunoServices;
import br.cefetrj.sisgee.control.TermoAditivoServices;
import br.cefetrj.sisgee.control.TermoEstagioServices;
import br.cefetrj.sisgee.model.entity.Aluno;
import br.cefetrj.sisgee.model.entity.TermoAditivo;
import br.cefetrj.sisgee.model.entity.TermoEstagio;
import br.cefetrj.sisgee.view.utils.UF;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet disponibiliza o termo aditivo e termo estágio ao pedir pra ser visualizado
 * @author Vinicius Paradellas
 */
@WebServlet("/VisualizarTermoEAditivo")
public class VisualizarTermoEAditivo extends HttpServlet {

    /**
     * 
     * @param request um objeto HttpServletRequest que contém a solicitação feita pelo cliente do servlet.
     * @param response um objeto HttpServletResponse que contém a resposta que o servlet envia para o cliente
     * @throws ServletException se o pedido do service não puder ser tratado
     * @throws IOException se um erro de entrada ou saída for detectado quando o servlet manipula o pedido 
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String ide = req.getParameter("ide");
        String ida = req.getParameter("ida");
        String matricula = req.getParameter("matricula");
        UF[] uf = UF.asList();
        TermoEstagio termoEstagio=null;
        TermoAditivo termoAditivo=null;
        
        Aluno aluno=AlunoServices.buscarAlunoByMatricula(matricula);
        if(ide!=null)
        termoEstagio=TermoEstagioServices.buscarTermoEstagio(Integer.parseInt(ide));
        if(ida!=null)
        termoAditivo=TermoAditivoServices.buscarTermoAditivo(Integer.parseInt(ida));
        
        req.setAttribute("uf", uf);
                        
			/** Dados de aluno*/
                        req.setAttribute("alMatricula", aluno.getMatricula());
                        req.setAttribute("alNome", aluno.getPessoa().getNome());
                        req.setAttribute("alCampus", aluno.getCurso().getCampus().getNomeCampus());
                        req.setAttribute("alCurso", aluno.getCurso());
			
                        /** Dados de convenio*/
                        req.setAttribute("cvNumero", termoEstagio.getConvenio().getNumeroConvenio());
                        if(termoEstagio.getConvenio().getEmpresa()==null){
                            req.setAttribute("cvNome", termoEstagio.getConvenio().getPessoa().getNome());
                            req.setAttribute("tConvenio","pf");
                            req.setAttribute("cvCpfCnpj",termoEstagio.getConvenio().getPessoa().getCpf());
                            req.setAttribute("nomeAgenciada",termoEstagio.getNomeAgenciada());
                            
                        }else{
                            req.setAttribute("cvNome", termoEstagio.getConvenio().getEmpresa().getRazaoSocial());
                            req.setAttribute("tConvenio","pj");
                            req.setAttribute("agIntegracao",termoEstagio.getConvenio().getEmpresa().isAgenteIntegracao());
                            req.setAttribute("cvCpfCnpj", termoEstagio.getConvenio().getEmpresa().getCnpjEmpresa());
                            req.setAttribute("nomeAgenciada",termoEstagio.getNomeAgenciada());
                        }
                        
                        /** Dados de Vigência */
                        req.setAttribute("vidataInicioTermoEstagio",termoEstagio.getDataInicioTermoEstagio2());
                        req.setAttribute("vidataFimTermoEstagio",termoEstagio.getDataFimTermoEstagioVisu(termoAditivo));
                        
                        /** Dados de Carga Horária */
                        req.setAttribute("cacargaHorariaTermoEstagio",termoEstagio.getCargaHorariaTermoEstagioVisu(termoAditivo));
                        
                        /** Dados de Valor Bolsa */
                        req.setAttribute("vavalorBolsa",termoEstagio.getValorBolsaVisu(termoAditivo));
                        System.out.println("aqui"+termoEstagio.getValorBolsaVisu(termoAditivo));
                        /** Dados de Local */
                        req.getServletContext().setAttribute("enenderecoTermoEstagio",termoEstagio.getEnderecoTermoEstagioVisu(termoAditivo));
                        req.setAttribute("ennumeroEnderecoTermoEstagio",termoEstagio.getNumeroEnderecoTermoEstagioVisu(termoAditivo));
                        req.setAttribute("encomplementoEnderecoTermoEstagio",termoEstagio.getComplementoEnderecoTermoEstagioVisu(termoAditivo));
                        req.setAttribute("enbairroEnderecoTermoEstagio",termoEstagio.getBairroEnderecoTermoEstagioVisu(termoAditivo));
                        req.setAttribute("encidadeEnderecoTermoEstagio",termoEstagio.getCidadeEnderecoTermoEstagioVisu(termoAditivo));
                        req.setAttribute("enuf",termoEstagio.getEstadoEnderecoTermoEstagioVisu(termoAditivo));
                        req.setAttribute("encepEnderecoTermoEstagio",termoEstagio.getCepEnderecoTermoEstagioVisu(termoAditivo));
                        
                        /** Dados de Supervisor */
                        
                        req.setAttribute("eobrigatorio",termoEstagio.getEEstagioObrigatorio());
                        req.setAttribute("nomeSupervisor",termoEstagio.getNomeSupervisorVisu(termoAditivo));
                        req.setAttribute("cargoSupervisor",termoEstagio.getCargoSupervisorVisu(termoAditivo));
                        
                        /** Dados de Professor */
                        req.setAttribute("pfnomeprofessor",termoEstagio.getProfessorOrientadorVisu(termoAditivo));
                        
        req.getRequestDispatcher("/form_termo_Visualiza.jsp").forward(req, resp);
    }
}