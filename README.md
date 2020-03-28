# SocialDistancing
For server admins to guarantee safety and minimize the risk of the spread of COVID-19 on their servers, or just to have a fun April Fools event :)
Pushes players apart if they're too close.

**DEFAULT SETTINGS (in /plugins/SocialDistancing/config.yml)**
isEnabled: true
distance: 6 # Distance to be enforced between players (in blocks)
powerMultiplier: 1 # How strongly should the plugin force players to stay away from each other? Turning down the number will make the plugin push players more gently. Turn it up for memes
cancelYVelocity: false # Whether the plugin will give players velocity in the y-direction (useful if anticheat is going off or preventing players from exploiting the mechanic to fly)
sound: "ENTITY_EGG_THROW" # Sound to play when pushing players away from each other. Use NONE for no sound. The full list of sounds is at https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html

**NOTES:** 
OPs or players with the permission socialdistancing.disable are not affected by this plugin.
Whether social distancing is enabled (can be toggled in game by players with the permission socialdistancing.toggle with the command /toggleSocialDistancing)
The plugin can be reloaded from config using /reloadSocialDistancing (permission: socialdistancing.reload)