package com.autonomy.frontend.configuration;

import com.autonomy.aci.actions.common.GetVersionProcessor;
import com.autonomy.aci.actions.common.Version;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.nonaci.ServerDetails;
import com.autonomy.nonaci.indexing.IndexCommand;
import com.autonomy.nonaci.indexing.IndexingException;
import com.autonomy.nonaci.indexing.IndexingService;
import com.autonomy.nonaci.indexing.impl.IndexCommandImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import static com.autonomy.frontend.configuration.ServerConfigTest.IsAciParameter.aciParameter;
import static com.autonomy.frontend.configuration.ServerConfigTest.SetContainingItems.isSetWithItems;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
public class ServerConfigTest {

    private AciService aciService;
    private IndexingService indexingService;

    @Before
    public void setUp() {
        aciService = mock(AciService.class);
        indexingService = mock(IndexingService.class);
    }

    @Test
    public void testValidate() {
        final String productType = "IDOLSASS";
        final Version version = new Version();
        version.setProductTypes(Collections.singleton(productType));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 6666)),
            argThat(isSetWithItems(aciParameter("action", "GetVersion"))),
            argThat(any(GetVersionProcessor.class))
        )).thenReturn(version);

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 6666)),
            argThat(isSetWithItems(aciParameter("action", "GetChildren"))),
            argThat(any(PortsResponseProcessor.class))
        )).thenReturn(new PortsResponse(6666, 0, 6668));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 6668)),
            argThat(isSetWithItems(aciParameter("action", "GetStatus"))),
            argThat(any(NoopProcessor.class))
        )).thenReturn(true);

        final ServerConfig serverConfig = new ServerConfig.Builder()
            .setHost("example.com")
            .setPort(6666)
            .setProductType(productType)
            .build();

        assertTrue(serverConfig.validate(aciService, null).isValid());
    }

    @Test
    public void testValidateWithIndexPort() {
        final String indexErrorMessage = "Bad command or file name";
        final String productType = "INDEXINGMACHINE";

        final Version version = new Version();
        version.setProductTypes(Collections.singleton(productType));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 7666)),
            argThat(isSetWithItems(aciParameter("action", "GetVersion"))),
            argThat(any(GetVersionProcessor.class))
        )).thenReturn(version);

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 7666)),
            argThat(isSetWithItems(aciParameter("action", "GetStatus"))),
            argThat(any(PortsResponseProcessor.class))
        )).thenReturn(new PortsResponse(7666, 7667, 7668));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 7668)),
            argThat(isSetWithItems(aciParameter("action", "GetStatus"))),
            argThat(any(NoopProcessor.class))
        )).thenReturn(true);

        when(indexingService.executeCommand(
            argThat(new IsServerDetails("example.com", 7667)),
            argThat(any(IndexCommand.class))
        )).thenThrow(new IndexingException(indexErrorMessage));

        final ServerConfig serverConfig = new ServerConfig.Builder()
            .setHost("example.com")
            .setPort(7666)
            .setProductType(productType)
            .setIndexErrorMessage(indexErrorMessage)
            .build();

        assertTrue(serverConfig.validate(aciService, indexingService).isValid());
    }

    @Test
    public void testValidateWithIncorrectIndexErrorMessage() {
        final String productType = "INDEXINGMACHINE";

        final Version version = new Version();
        version.setProductTypes(Collections.singleton(productType));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 7666)),
            argThat(isSetWithItems(aciParameter("action", "GetVersion"))),
            argThat(any(GetVersionProcessor.class))
        )).thenReturn(version);

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 7666)),
            argThat(isSetWithItems(aciParameter("action", "GetStatus"))),
            argThat(any(PortsResponseProcessor.class))
        )).thenReturn(new PortsResponse(7666, 7667, 7668));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 7668)),
            argThat(isSetWithItems(aciParameter("action", "GetStatus"))),
            argThat(any(NoopProcessor.class))
        )).thenReturn(true);

        when(indexingService.executeCommand(
            argThat(new IsServerDetails("example.com", 7667)),
            argThat(any(IndexCommand.class))
        )).thenThrow(new IndexingException("ERRORPARAMBAD"));

        final ServerConfig serverConfig = new ServerConfig.Builder()
            .setHost("example.com")
            .setPort(7666)
            .setProductType(productType)
            .setIndexErrorMessage("Bad command or file name")
            .build();

        assertFalse(serverConfig.validate(aciService, indexingService).isValid());
    }

    @Test
    public void testValidateWithWrongVersion() {
        final Version version = new Version();
        version.setProductTypes(Collections.singleton("IDOLSASS"));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 6666)),
            argThat(isSetWithItems(aciParameter("action", "GetVersion"))),
            argThat(any(GetVersionProcessor.class))
        )).thenReturn(version);

        // no further stubbing required because we won't get that far

        final ServerConfig serverConfig = new ServerConfig.Builder()
            .setHost("example.com")
            .setPort(6666)
            .setProductType("IDOLSAAS")
            .build();

        assertFalse(serverConfig.validate(aciService, null).isValid());
    }

    @Test
    public void testValidateWithNoServicePort() {
        final String productType = "IDOLSASS";
        final Version version = new Version();
        version.setProductTypes(Collections.singleton(productType));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 6666)),
            argThat(isSetWithItems(aciParameter("action", "GetVersion"))),
            argThat(any(GetVersionProcessor.class))
        )).thenReturn(version);

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 6666)),
            argThat(isSetWithItems(aciParameter("action", "GetChildren"))),
            argThat(any(PortsResponseProcessor.class))
        )).thenReturn(new PortsResponse(6666, 0, 0));


        final ServerConfig serverConfig = new ServerConfig.Builder()
            .setHost("example.com")
            .setPort(6666)
            .setProductType(productType)
            .build();

        assertFalse(serverConfig.validate(aciService, null).isValid());
    }

    @Test
    public void testValidateWithMissingIndexPort() {
        final String indexErrorMessage = "Bad command or file name";
        final String productType = "INDEXINGMACHINE";

        final Version version = new Version();
        version.setProductTypes(Collections.singleton(productType));

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 7666)),
            argThat(isSetWithItems(aciParameter("action", "GetVersion"))),
            argThat(any(GetVersionProcessor.class))
        )).thenReturn(version);

        when(aciService.executeAction(
            argThat(new IsAciServerDetails("example.com", 7666)),
            argThat(isSetWithItems(aciParameter("action", "GetStatus"))),
            argThat(any(PortsResponseProcessor.class))
        )).thenReturn(new PortsResponse(7666, 0, 7668));

        final ServerConfig serverConfig = new ServerConfig.Builder()
            .setHost("example.com")
            .setPort(7666)
            .setProductType(productType)
            .setIndexErrorMessage(indexErrorMessage)
            .build();

        assertFalse(serverConfig.validate(aciService, indexingService).isValid());
    }

    @Test
    public void testValidateWithInvalidHost() {
        final ServerConfig serverConfig = new ServerConfig.Builder()
            .setHost("")
            .setPort(6666)
            .setProductType("IDOLSAAS")
            .build();

        assertFalse(serverConfig.validate(aciService, null).isValid());
    }

    @Test
    public void testValidateWithInvalidPort() {
        final ServerConfig serverConfig = new ServerConfig.Builder()
            .setHost("example.com")
            .setPort(0)
            .setProductType("IDOLSAAS")
            .build();

        assertFalse(serverConfig.validate(aciService, null).isValid());
    }

    static class IsAciParameter extends ArgumentMatcher<AciParameter> {

        private final String name;
        private final String value;

        private IsAciParameter(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        @Factory
        static IsAciParameter aciParameter(final String name, final String value) {
            return new IsAciParameter(name, value);
        }

        @Override
        public boolean matches(final Object argument) {
            if(!(argument instanceof AciParameter)) {
                return false;
            }

            final AciParameter parameter = (AciParameter) argument;

            return name.equalsIgnoreCase(parameter.getName())
                && value.equalsIgnoreCase(parameter.getValue());
        }
    }

    static class SetContainingItems<T> extends ArgumentMatcher<Set<? super T>> {

        private final Set<? super T> set = new HashSet<>();
        private Matcher<? super T> matcher;

        @SafeVarargs
        private SetContainingItems(final T... items) {
            set.addAll(Arrays.asList(items));
        }

        private SetContainingItems(final Matcher<? super T> matcher) {
            this.matcher = matcher;
        }

        @SafeVarargs
        @Factory
        static <T> SetContainingItems<T> isSetWithItems(final T... items) {
            return new SetContainingItems<>(items);
        }

        @SafeVarargs
        @Factory
        static <T> Matcher<Set<T>> isSetWithItems(final Matcher<? super T>... matchers) {
            final List<Matcher<? super Set<T>>> all = new ArrayList<>(matchers.length);

            for (final Matcher<? super T> elementMatcher : matchers) {
                // Doesn't forward to hasItem() method so compiler can sort out generics.
                all.add(new SetContainingItems<>(elementMatcher));
            }

            return allOf(all);
        }

        @Override
        public boolean matches(final Object item) {
            if(!(item instanceof Set)) {
                return false;
            }

            final Set<?> itemAsSet = (Set<?>) item;

            if(matcher == null) {
                return set.containsAll(itemAsSet);
            }
            else {
                for(final Object setItem : itemAsSet) {
                    if (matcher.matches(setItem)) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    private static class IsAciServerDetails extends ArgumentMatcher<AciServerDetails> {

        private final String host;
        private final int port;

        private IsAciServerDetails() {
            this(null, -1);
        }

        private IsAciServerDetails(final int port) {
            this(null, port);
        }

        private IsAciServerDetails(final String host) {
            this(host, -1);
        }

        private IsAciServerDetails(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public boolean matches(final Object o) {
            if(!(o instanceof AciServerDetails)) {
                return false;
            }

            final AciServerDetails serverDetails = (AciServerDetails) o;

            boolean result = true;

            if(host != null) {
                result = host.equals(serverDetails.getHost());
            }

            if(port != -1) {
                result = result && port == serverDetails.getPort();
            }

            return result;
        }
    }

    // duplicate all this due to deficiencies of the Autonomy APIs
    private static class IsServerDetails extends ArgumentMatcher<ServerDetails> {

        private final String host;
        private final int port;

        private IsServerDetails() {
            this(null, -1);
        }

        private IsServerDetails(final int port) {
            this(null, port);
        }

        private IsServerDetails(final String host) {
            this(host, -1);
        }

        private IsServerDetails(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public boolean matches(final Object o) {
            if(! (o instanceof ServerDetails)) {
                return false;
            }

            final ServerDetails serverDetails = (ServerDetails) o;

            boolean result = true;

            if(host != null) {
                result = host.equals(serverDetails.getHost());
            }

            if(port != -1) {
                result = result && port == serverDetails.getPort();
            }

            return result;
        }
    }
}
