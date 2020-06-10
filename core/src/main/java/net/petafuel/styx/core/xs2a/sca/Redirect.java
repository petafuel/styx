package net.petafuel.styx.core.xs2a.sca;

public class Redirect implements SCAApproach{

    private String authoriseLink;

    public Redirect(String link)
    {
        this.authoriseLink = link;
    }

    public String getAuthoriseLink() {
        return authoriseLink;
    }

    public void setAuthoriseLink(String authoriseLink) {
        this.authoriseLink = authoriseLink;
    }
}
