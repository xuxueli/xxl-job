package com.xxl.job.admin.framework.oidc;

import com.xxl.job.admin.framework.mapper.XxlJobUserMapper;
import com.xxl.job.admin.framework.model.XxlJobUser;
import com.xxl.sso.core.model.LoginInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OidcLoginServiceTest {

    @Mock
    private XxlJobUserMapper xxlJobUserMapper;
    @Spy
    private OidcProperties oidcProperties = new OidcProperties();
    @InjectMocks
    private OidcLoginService oidcLoginService;

    private static XxlJobUser user(int id, String username, int role) {
        XxlJobUser user = new XxlJobUser();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("pwd_hash");
        user.setRole(role);
        return user;
    }

    @Test
    public void existUserWithoutRolesClaimKeepRole() {
        when(xxlJobUserMapper.loadByUserName("alice")).thenReturn(user(7, "alice", 1));

        LoginInfo loginInfo = oidcLoginService.login(Map.of("preferred_username", "alice"));

        assertEquals("7", loginInfo.getUserId());
        assertNotNull(loginInfo.getSignature());
        verify(xxlJobUserMapper, never()).update(any());
        verify(xxlJobUserMapper, never()).save(any());
    }

    @Test
    public void autoCreateUser() {
        when(xxlJobUserMapper.loadByUserName("bob")).thenReturn(null);
        doAnswer(invocation -> {
            invocation.<XxlJobUser>getArgument(0).setId(9);
            return 1;
        }).when(xxlJobUserMapper).save(any());

        LoginInfo loginInfo = oidcLoginService.login(Map.of("preferred_username", "bob"));

        ArgumentCaptor<XxlJobUser> captor = ArgumentCaptor.forClass(XxlJobUser.class);
        verify(xxlJobUserMapper).save(captor.capture());
        XxlJobUser saved = captor.getValue();
        assertEquals("bob", saved.getUsername());
        assertEquals(0, saved.getRole());
        assertNull(saved.getPermission());
        assertEquals(64, saved.getPassword().length());
        assertEquals("9", loginInfo.getUserId());
    }

    @Test
    public void autoCreateDisabled() {
        oidcProperties.setAutoCreateUser(false);
        when(xxlJobUserMapper.loadByUserName("bob")).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> oidcLoginService.login(Map.of("preferred_username", "bob")));
        verify(xxlJobUserMapper, never()).save(any());
    }

    @Test
    public void rolesClaimGrantAdmin() {
        oidcProperties.setRolesClaim("realm_access.roles");
        oidcProperties.setAdminRole("xxl-job-admin");
        when(xxlJobUserMapper.loadByUserName("alice")).thenReturn(user(7, "alice", 0));

        oidcLoginService.login(Map.of(
                "preferred_username", "alice",
                "realm_access", Map.of("roles", List.of("offline_access", "xxl-job-admin"))));

        ArgumentCaptor<XxlJobUser> captor = ArgumentCaptor.forClass(XxlJobUser.class);
        verify(xxlJobUserMapper).update(captor.capture());
        assertEquals(1, captor.getValue().getRole());
        assertNull(captor.getValue().getPassword());
    }

    @Test
    public void rolesClaimRevokeAdmin() {
        oidcProperties.setRolesClaim("roles");
        oidcProperties.setAdminRole("xxl-job-admin");
        when(xxlJobUserMapper.loadByUserName("alice")).thenReturn(user(7, "alice", 1));

        oidcLoginService.login(Map.of("preferred_username", "alice", "roles", "other-role"));

        ArgumentCaptor<XxlJobUser> captor = ArgumentCaptor.forClass(XxlJobUser.class);
        verify(xxlJobUserMapper).update(captor.capture());
        assertEquals(0, captor.getValue().getRole());
    }

    @Test
    public void rolesClaimNewAdminUser() {
        oidcProperties.setRolesClaim("roles");
        oidcProperties.setAdminRole("xxl-job-admin");
        when(xxlJobUserMapper.loadByUserName("carol")).thenReturn(null);

        oidcLoginService.login(Map.of("preferred_username", "carol", "roles", "xxl-job-admin"));

        ArgumentCaptor<XxlJobUser> captor = ArgumentCaptor.forClass(XxlJobUser.class);
        verify(xxlJobUserMapper).save(captor.capture());
        assertEquals(1, captor.getValue().getRole());
    }

    @Test
    public void rolesClaimWithoutAdminRoleNotSync() {
        oidcProperties.setRolesClaim("roles");
        when(xxlJobUserMapper.loadByUserName("alice")).thenReturn(user(7, "alice", 1));

        oidcLoginService.login(Map.of("preferred_username", "alice", "roles", List.of("other-role")));

        verify(xxlJobUserMapper, never()).update(any());
    }

    @Test
    public void rolesClaimByFullKey() {
        oidcProperties.setRolesClaim("https://example.com/roles");
        oidcProperties.setAdminRole("xxl-job-admin");
        when(xxlJobUserMapper.loadByUserName("alice")).thenReturn(user(7, "alice", 0));

        oidcLoginService.login(Map.of("preferred_username", "alice", "https://example.com/roles", List.of("xxl-job-admin")));

        ArgumentCaptor<XxlJobUser> captor = ArgumentCaptor.forClass(XxlJobUser.class);
        verify(xxlJobUserMapper).update(captor.capture());
        assertEquals(1, captor.getValue().getRole());
    }

    @Test
    public void usernameInvalid() {
        assertThrows(IllegalStateException.class, () -> oidcLoginService.login(Map.of("sub", "123")));
        assertThrows(IllegalStateException.class, () -> oidcLoginService.login(Map.of("preferred_username", "a".repeat(51))));
        for (String username : List.of("", " alice", "-alice", "ali ce", "alice\r\nfake", "<script>", "张三")) {
            assertThrows(IllegalStateException.class, () -> oidcLoginService.login(Map.of("preferred_username", username)), username);
        }
        verifyNoInteractions(xxlJobUserMapper);
    }

    @Test
    public void usernameValid() {
        for (String username : List.of("a", "alice.w_1", "alice@example.com", "a".repeat(50))) {
            when(xxlJobUserMapper.loadByUserName(username)).thenReturn(user(7, username, 0));
            assertEquals("7", oidcLoginService.login(Map.of("preferred_username", username)).getUserId());
        }
    }

    @Test
    public void usernameCaseMismatchRejected() {
        when(xxlJobUserMapper.loadByUserName("admin")).thenReturn(user(1, "Admin", 1));

        assertThrows(IllegalStateException.class, () -> oidcLoginService.login(Map.of("preferred_username", "admin")));
        verify(xxlJobUserMapper, never()).save(any());
        verify(xxlJobUserMapper, never()).update(any());
    }

    @Test
    public void parseRoles() {
        Map<String, Object> claims = Map.of(
                "roles", "r1",
                "realm_access", Map.of("roles", List.of("r2", "r3")),
                "https://example.com/roles", List.of("r4"));

        assertEquals(List.of("r1"), OidcLoginService.parseRoles(claims, "roles"));
        assertEquals(List.of("r2", "r3"), OidcLoginService.parseRoles(claims, "realm_access.roles"));
        assertEquals(List.of("r4"), OidcLoginService.parseRoles(claims, "https://example.com/roles"));
        assertEquals(List.of(), OidcLoginService.parseRoles(claims, "roles.sub"));
        assertEquals(List.of(), OidcLoginService.parseRoles(claims, "missing"));
    }

}
