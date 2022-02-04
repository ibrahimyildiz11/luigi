package be.vdab.luigi.controllers;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.KoersClientException;
import be.vdab.luigi.forms.VanTotPrijsForm;
import be.vdab.luigi.services.EuroService;
import be.vdab.luigi.services.PizzaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Stream;

@Controller
@RequestMapping("pizzas")
class PizzaController {
    public final Pizza[] pizzas = {
            new Pizza(1, "Prosciutto", BigDecimal.valueOf(4), true),
            new Pizza(2, "Margherita", BigDecimal.valueOf(5), false),
            new Pizza(3, "Calzone", BigDecimal.valueOf(4), false),
    };

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EuroService euroService;
    private final PizzaService pizzaService;


    public PizzaController(EuroService euroService, PizzaService pizzaService) {
        this.euroService = euroService;
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public ModelAndView pizzas() {
        return new ModelAndView("pizzas", "pizzas", pizzaService.findAll());
    }

    @GetMapping("{id}")
    public ModelAndView pizza(@PathVariable long id) {
        var modelAndView = new ModelAndView("pizza");
        pizzaService.findById(id).ifPresent(pizza -> {
            modelAndView.addObject("pizza", pizza);
            try {
                modelAndView.addObject(
                        "inDollar", euroService.naarDollar(pizza.getPrijs()));
            } catch (KoersClientException ex) {
                logger.error("Kan dollar koers niet lezen" , ex);
            }
        });
        /*Arrays.stream(pizzas).filter(pizza -> pizza.getId() ==id).findFirst()
                .ifPresent(pizza -> {
                    modelAndView.addObject("pizza", pizza);
                    try {
                        modelAndView.addObject(
                                "inDollar", euroService.naarDollar(pizza.getPrijs()));
                    } catch (KoersClientException ex) {
                        logger.error("Kan dollar koers niet lezen", ex);
                        // Hier komt later code die de exception verwerkt.
                    }
                });*/
        return modelAndView;
    }
    /*
    private Stream<BigDecimal> uniekePrijzen() {
        return Arrays.stream(pizzas).map(Pizza::getPrijs).distinct().sorted();
    }*/
    @GetMapping("prijzen") public ModelAndView prijzen(){
        return new ModelAndView("prijzen", "prijzen",pizzaService.findUniekePrijzen());
    }

    /*private Stream<Pizza> pizzaMetPrijs(BigDecimal prijs) {
        return Arrays.stream(pizzas)
                .filter(pizza -> pizza.getPrijs().compareTo(prijs) == 0);
    }*/

    @GetMapping("prijzen/{prijs}")
    public ModelAndView pizzasMetEenPrijs(@PathVariable BigDecimal prijs) {
        return new ModelAndView("prijzen","pizzas", pizzaService.findByPrijs(prijs))
                .addObject("prijzen", pizzaService.findUniekePrijzen());
    }

    @GetMapping("aantalpizzasperprijs")
    public ModelAndView aantalPizzasPerPrijs() {
        return new ModelAndView("aantalpizzasperprijs","aantalPizzasPerPrijs",
                pizzaService.findAantalPizzasPerPrijs());
    }

    @GetMapping("vantotprijs/form")
    public ModelAndView vanTotPrijsForm() {
        return new ModelAndView("vantotprijs")
                .addObject(new VanTotPrijsForm(BigDecimal.ONE, BigDecimal.TEN));
    }

    @GetMapping("vantotprijs")
    public ModelAndView vanTotPrijs(@Valid VanTotPrijsForm form, Errors errors) {
        var modelAndView = new ModelAndView("vantotprijs");
        if (errors.hasErrors()) {
            return modelAndView;
        }
        return modelAndView.addObject("pizzas", pizzaService.findByPrijsBetween(
                form.van(), form.tot()));
    }

    @GetMapping("toevoegen/form")
    public ModelAndView toevoegenForm() {
        return new ModelAndView("toevoegen")
                .addObject(new Pizza(0, "", null, false));
    }
    @PostMapping
    public String toevoegen(@Valid Pizza pizza, Errors errors, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "toevoegen";
        }
        redirect.addAttribute("idNieuwePizza", pizzaService.create(pizza));
        return "redirect:/pizzas";
    }
}
