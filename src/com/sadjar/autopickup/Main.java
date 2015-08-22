package com.sadjar.autopickup;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main
  extends JavaPlugin
  implements Listener
{
  HashMap<Location, Player> breaks = new HashMap();
  
  public void onEnable()
  {
    Bukkit.getPluginManager().registerEvents(this, this);
  }
  
  @EventHandler
  public void onItemSpawn(ItemSpawnEvent event)
  {
    for (Location loc : this.breaks.keySet()) {
      if ((loc.getWorld().equals(event.getLocation().getWorld())) && 
        (loc.distance(event.getLocation()) < 0.7D))
      {
        if (!canGive((Player)this.breaks.get(loc), event.getEntity().getItemStack().getType())) {
          break;
        }
        event.setCancelled(true);
        ((Player)this.breaks.get(loc)).getInventory().addItem(new ItemStack[] { event.getEntity().getItemStack() });
        
        break;
      }
    }
  }
  
  public boolean canGive(Player player, Material type)
  {
    HashMap<Integer, ? extends ItemStack> items = player.getInventory().all(type);
    for (Iterator localIterator = items.keySet().iterator(); localIterator.hasNext();)
    {
      int slot = ((Integer)localIterator.next()).intValue();
      if (player.getInventory().getItem(slot).getAmount() < type.getMaxStackSize()) {
        return true;
      }
    }
    return false;
  }
  
  @EventHandler
  public void onPlayerBreak(final BlockBreakEvent event)
  {
    if (event.getPlayer().hasPermission("autopickup.pickup"))
    {
      this.breaks.put(event.getBlock().getLocation().add(0.5D, 0.5D, 0.5D), event.getPlayer());
      new BukkitRunnable()
      {
        public void run()
        {
          Main.this.breaks.remove(event.getPlayer());
        }
      }.runTaskLater(this, 1L);
    }
  }
  
  public void saveUrl(String filename, String urlString)
    throws MalformedURLException, IOException
  {
    BufferedInputStream in = null;
    FileOutputStream fout = null;
    try
    {
      in = new BufferedInputStream(new URL(urlString).openStream());
      fout = new FileOutputStream(filename);
      
      byte[] data = new byte[1024];
      int count;
      while ((count = in.read(data, 0, 1024)) != -1)
      {
        int count1 = 0;
        fout.write(data, 0, count1);
      }
    }
    finally
    {
      if (in != null) {
        in.close();
      }
      if (fout != null) {
        fout.close();
      }
    }
  }
}

