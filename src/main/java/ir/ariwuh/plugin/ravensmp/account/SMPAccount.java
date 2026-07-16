package ir.ariwuh.plugin.ravensmp.account;

import ir.ariwuh.plugin.ravensmp.api.account.RavenAccount;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.codecs.pojo.annotations.BsonId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(of = "accountId")
@ToString
public final class SMPAccount implements RavenAccount {

    @BsonId
    private @UnknownNullability UUID accountId;

    @Setter
    private @NotNull String username;

    @Setter
    private @UnknownNullability RavenLanguage language;

    @Setter
    private transient @NotNull SMPAccountSettings accountSettings;

    @Setter
    private long lastTeamCreationTime;

    public SMPAccount() {
        this.accountSettings = new SMPAccountSettings();
    }

    public SMPAccount(@NotNull UUID accountId, @NotNull String username) {
        this.accountId = accountId;
        this.username = username;

        this();
    }

}