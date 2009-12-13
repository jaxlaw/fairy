package com.mewmew.fairy.v1.book;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.json.JsonOutput;
import com.mewmew.fairy.v1.json.OutputFormat;
import com.mewmew.fairy.v1.map.MapFunction;
import com.mewmew.fairy.v1.pipe.BaseObjectPipe;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.spell.BaseLineSpell;
import com.mewmew.fairy.v1.spell.Help;

@Help(desc = "run a command on a set of remote hosts")
public class Dsh extends BaseLineSpell<Map<String, Object>> 
  implements MapFunction<String, Map<String, Object>> {

  @Param(option = "O", name = "format", desc = "PRETTY, LINE, COMPACT", defaultValue = "LINE")
  private OutputFormat outputFormat;
  
  @Param(option = "u", name = "user", desc = "user to log in as", defaultValue = "david")
  private String user;

  @Param(option = "c", name = "command", desc = "command to run", defaultValue = "pwd")
  private String command;
  
  private List<String> hosts = newArrayList();
  
  private String[] possibleKeyLocations = new String[] {"/.ssh/id_dsa", "/.ssh/id_rsa" };
  
  private JSch jsch = new JSch();
  
  private final Logger log = Logger.getLogger(Dsh.class);

  private ExecutorService executor = Executors.newCachedThreadPool();
  
  static {
    BasicConfigurator.configure();
  }
  
  @Override
  public void before() {
    super.before();
    log.setLevel(Level.INFO);
    try {
      initializeSshKey();
    } catch (JSchException e) {
      log.error("Unable to find ssh key", e);
    }
  }
  
  @Override
  protected Output<Map<String, Object>> createOutput(OutputStream out)
      throws IOException {
    return JsonOutput.createOutput(out, outputFormat);
  }

  @Override
  protected ObjectPipe<String, Map<String, Object>> createPipe() {
    return new BaseObjectPipe<String, Map<String, Object>>(this);
  }

  Output<Map<String, Object>> output;
  
  @Override
  public void each(final String host, final Output<Map<String, Object>> output)
      throws IOException {
    hosts.add(host.trim());
    this.output = output;
  }
  
  public void after() {
    final CountDownLatch latch = new CountDownLatch(hosts.size());
    for (final String host : hosts) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          try {
            log.debug(format("Connecting to '%s' as '%s'", host, user));
            Session s = getSession(host);
            s.connect();
            ChannelExec channel = (ChannelExec) s.openChannel("exec");
            channel.setCommand(command);
            channel.connect();
            log.debug(format("Connected to '%s' as '%s'", host, user));
            BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
              output.output(ImmutableMap.of("host", (Object) host, "line", (Object) line));
            }
          } catch (Exception e) {
            log.error(format("Error during ssh connection to: %s", host), e);
          } finally {
            log.debug(format("Finished with connection to %s", host));
            latch.countDown();
          }
        }
        
      });
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      log.info("Interrupted while waiting on ssh sessions", e);
    }
    super.after();
  }
  
  Session getSession(String host) throws Exception {
    Session session = jsch.getSession(user, host);
    session.setConfig("StrictHostKeyChecking", "no");
    return session;
  }
  
  private void initializeSshKey() throws JSchException {
    String userHome = System.getProperty("user.home");
    boolean keyFound = false;
    for (String possibleKey : possibleKeyLocations) {
      File f = new File(userHome + possibleKey);
      if (f.exists()) {
        jsch.addIdentity(f.getAbsolutePath());
        log.debug(format("Found ssh key at: %s", f.getAbsolutePath()));
        keyFound = true;
      }
    }
    if (!keyFound) {
      log.error("Unable to find ssh key");
    }
  }
}
