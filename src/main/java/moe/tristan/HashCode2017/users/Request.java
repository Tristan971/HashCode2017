package moe.tristan.HashCode2017.users;

import lombok.Builder;
import lombok.Data;

/**
 * Created by tristan on 23/02/2017.
 */
@Data
@Builder
public class Request {
    int videouid;
    int endpointuid;
    int multiplicity;
}
