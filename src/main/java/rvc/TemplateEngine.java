package rvc;

public abstract class TemplateEngine {

    public String render(Object object) {
        ModelAndView modelAndView = (ModelAndView) object;
        return render(modelAndView);
    }

    public abstract String render(ModelAndView modelAndView);

}