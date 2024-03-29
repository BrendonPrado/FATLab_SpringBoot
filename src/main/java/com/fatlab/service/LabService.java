package com.fatlab.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fatlab.domain.HorarioComecoFimAula;
import com.fatlab.domain.Lab;
import com.fatlab.dto.LaboratorioDTO;
import com.fatlab.repositories.LabRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabService extends GenericServiceImpl<Lab> {

    @Autowired
    private HoraService horaService;

    public Lab findByNumero(Integer numero) {
        return ((LabRepository) this.repo).findByNumero(numero);
    }

    public Lab fromDTO(LaboratorioDTO laboratorioDTO) {
        return new Lab(laboratorioDTO.getNumero(), laboratorioDTO.getCapacidade());
    }

    public void updateFromDTO(Integer id, LaboratorioDTO laboratorioDTO) {
        Lab lab = find(id);
        setInfosByDTO(lab, laboratorioDTO);
        save(lab);
    }

    private void setInfosByDTO(Lab lab, LaboratorioDTO laboratorioDTO) {
        lab.setCapacidade(laboratorioDTO.getCapacidade());
        lab.setNumero(laboratorioDTO.getNumero());
    }

    public void removeLabsNotInList(List<Lab> all, List<Lab> toPersist) {
        for (Lab lab : all) {
            if (!toPersist.contains(lab)) {
                all.remove(lab);
            }

        }
    }

    public List<Lab> findAllLabsComReserva(Date data, HorarioComecoFimAula horario) {
        List<Lab> labs = ((LabRepository) this.repo).findIndispLabs(data, horario);
        return labs;
    }

    public List<Lab> getAllDispLabs(Date data, Set<Integer> aulasNum) {
        List<Lab> allLabs = findAll();
        for (Integer aula : aulasNum) {
            List<Lab> labsIndisp = findAllLabsComReserva(data, this.horaService.findOrCreateHorario(aula,"Diurno"));
            labsIndisp.forEach(f -> {
                allLabs.remove(f);
            });
        }
        return allLabs;
    }

    public List<Lab> findAllLabsDisponiveisNoMes(int mes, Set<Integer> dias, Set<Integer> aulasNum) {

        Set<Lab> labsagr = verificarDiasDoMesEmAlgumasAulas(mes, dias, aulasNum);
        return removerLabsFromAllList(labsagr);

    }

    public Set<Lab> verificarDiasDoMesEmAlgumasAulas(int mes, Set<Integer> dias, Set<Integer> aulasNum) {
        Calendar c = SetToInitialDate(mes);
        Set<Lab> labsUsados = new HashSet<>();

        for (int i = 0; i < c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            if (dias.contains(c.get(Calendar.DAY_OF_WEEK))) {
                  for (Integer aula : aulasNum) {
                    labsUsados.addAll(findAllLabsComReserva(c.getTime(), this.horaService.findOrCreateHorario(aula,"Diurno")));
                }
            }
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        }
        return labsUsados;
    }

    public Calendar SetToInitialDate(int mes) {
        Calendar c = new GregorianCalendar();
        int diaHoje = c.get(Calendar.MONTH);
        if (mes == diaHoje) {
            c.set(Calendar.DAY_OF_MONTH, diaHoje);
        } else {
            c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
        }
        c.set(Calendar.MONTH, mes);
        return c;
    }

    private List<Lab> removerLabsFromAllList(Set<Lab> labsagr) {
        List<Lab> labs = findAll();
        labsagr.forEach(f -> {
            labs.removeAll(labsagr);
            });
        return labs;
    }

	public boolean labInLabs(Lab lab, Set<Lab> labs) {
        for (Lab l : labs) {
            if(l.getId().equals(lab.getId())){
                return true;
            }
        }
        return false;
	}
}
