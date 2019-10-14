package net.petafuel.styx.core.xs2a.sca;

public class Redirect implements SCAApproach{

    private String redirectLink;

    public Redirect(String link)
    {
        this.redirectLink = link;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }
}
