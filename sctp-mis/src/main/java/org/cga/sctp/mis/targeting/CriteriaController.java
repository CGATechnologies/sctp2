package org.cga.sctp.mis.targeting;

import org.cga.sctp.mis.core.SecuredBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/targeting/criteria")
public class CriteriaController extends SecuredBaseController {

    private static final List<Criteria> FORMULAS = new LinkedList<>() {{
        add(new Criteria("SCTP Criteria", "formula 1goes here", true));
        add(new Criteria("Criteria 2", "formula goes here", false));
    }};

    @GetMapping
    public ModelAndView geCriteria() {
        return new ModelAndView("targeting/criteria/index")
                .addObject("criteria", FORMULAS);
    }

    @GetMapping("/new")
    public ModelAndView newCriteria() {
        return view("targeting/criteria/new");
    }
}
