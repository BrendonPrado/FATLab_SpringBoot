package com.fatlab.resource;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import com.fatlab.domain.Lab;
import com.fatlab.domain.Materia;
import com.fatlab.domain.Reserva;
import com.fatlab.dto.ReservaDTO;
import com.fatlab.dto.ReservaMesDTO;
import com.fatlab.service.LabService;
import com.fatlab.service.MateriaService;
import com.fatlab.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/reservas")
public class ReservaResource {

    @Autowired
    private ReservaService service;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private LabService labService;

    @Secured("ROLE_PROFESSOR")
    @RequestMapping(value = "/dia",method = RequestMethod.POST)
    public ResponseEntity<Void> save(@RequestBody @Valid ReservaDTO reservaDTO) {
        service.saveReservaFromDTO(reservaDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").buildAndExpand().toUri();

        return ResponseEntity.created(uri).build();
    }

    @Secured("ROLE_PROFESSOR")
    @RequestMapping(value = "/mes",method = RequestMethod.POST)
    public ResponseEntity<Void> saveMes(@RequestBody @Valid ReservaMesDTO reservaDTO) {
        service.saveReservaFromDTOMes(reservaDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("").buildAndExpand().toUri();

        return ResponseEntity.created(uri).build();
    }

    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Reserva>> findAll() {

        return ResponseEntity.ok().body(service.findAll());
    }

    @Secured({ "ROLE_ADMIN", "ROLE_PROFESSOR", "ROLE_ALUNO" })
    @GetMapping(value = "/materias/{id}")
    public ResponseEntity<List<Reserva>> findReservasMateria(@PathVariable Integer id) {
        Materia materia = materiaService.find(id);
        List<Reserva> reservas = service.findAllByMateria(materia);
        return ResponseEntity.ok().body(reservas);
    }

    @Secured({ "ROLE_ADMIN", "ROLE_PROFESSOR", "ROLE_ALUNO" })
    @GetMapping(value = "/labs/{id}")
    public ResponseEntity<List<Reserva>> findReservasLab(@PathVariable Integer id) {
        Lab lab = labService.find(id);
        List<Reserva> reservas = service.findAllByLab(lab);
        return ResponseEntity.ok().body(reservas);
    }

    @Secured({ "ROLE_ADMIN", "ROLE_PROFESSOR"})
    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        this.service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
