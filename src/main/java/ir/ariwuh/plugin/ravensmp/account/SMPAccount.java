package ir.ariwuh.plugin.ravensmp.account;

import ir.ariwuh.plugin.ravensmp.api.account.RavenAccount;
import lombok.*;
import lombok.experimental.Accessors;
import org.bson.codecs.pojo.annotations.BsonId;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Accessors(fluent = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = "accountId")
@ToString
public final class SMPAccount implements RavenAccount {

    @BsonId
    private @NotNull UUID accountId;

    @Setter
    private @NotNull String username;

}