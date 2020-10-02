package net.petafuel.styx.core.xs2a.sca;

public class OAuth2 implements SCAApproach {

    private String authoriseLink;

    public OAuth2(String link) {
        this.authoriseLink = link;
    }

    public String getAuthoriseLink() {
        return authoriseLink;
    }

    public void setAuthoriseLink(String authoriseLink) {
        this.authoriseLink = authoriseLink;
    }
}
