**Welcome to the Damage Tweaker GitHub!**
The mod has been released just when im writing this text. Any purpose to adding new features or enhancing existing ones
are welcomed!

-----
# GUIDE FOR DEVS USE:

### Installing the API in your mod.

Add this repository to the gradle:

maven {
url 'https://maven.pkg.github.com/wachipayox/*'
}

For adding the mod the package is:
"com.wachi.damagetweaker:damage-tweaker:VERSION"

---
The maven info for packages can be found in:
https://github.com/wachipayox?tab=packages&repo_name=damage-tweaker


_I recommend only adding the mod to the compile and not the runtime (when publishing),
for not creating conflicts with other mods that uses this one._

---
### Modifying values

Now that you have the mod in your gradle, the class you are interested in is in DamageConfig.
There is a Map called def_map, that is the default values map, when the user doesn't have a specific config for a damage
cooldown property, the game tries to get a value from this map before obtaining the general/default value (priority explained in
the following section). Enter to the DamageConfig class and see the def_map declaration for understanding the map syntax.
If you put an entity/damage that doesn't exist there will be no problems!

----------

#### **References:** 
Also, you can use "any mob" for making a reference to any source entity in the game or "any damage" to make a reference to
any damage source in the game. You can't use "any damage" at the same time of "any mob" because that is already declared in the default values that are in the "damage_tweaker-server.toml" file. That options can be overlapped using the Config class, that
has variables like def_cooldown... but I recommend not touching them.

-----
For modifying the map you can do it whenever you want, it's a static declaration so you can do it at the server start or even
your mod common setup.
