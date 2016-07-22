package rvc;


public class ModelAndView {

    private Object model;
    String viewName;

    public ModelAndView(Object model, String viewName) {
        this.model = model;
        this.viewName = viewName;
    }

    public Object getModel() {
        return model;
    }

    public String getViewName() {
        return viewName;
    }

}
