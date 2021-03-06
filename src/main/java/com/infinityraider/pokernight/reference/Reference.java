package com.infinityraider.pokernight.reference;

public interface Reference {

	String MOD_NAME = /*^${mod.name}^*/ "Pokernight";
	String MOD_ID = /*^${mod.id}^*/ "pokernight";
	String AUTHOR = /*^${mod.author}^*/ "InfinityRaider";

	String VER_MAJOR = /*^${mod.version_major}^*/ "0";
	String VER_MINOR = /*^${mod.version_minor}^*/ "0";
	String VER_PATCH = /*^${mod.version_patch}^*/ "0";
	String MOD_VERSION = /*^${mod.version}^*/ "0.0.0";
	String VERSION = /*^${mod.version_minecraft}-${mod.version}^*/ "0.0-0.0.0";

	String CLIENT_PROXY_CLASS = "com.infinityraider.pokernight.proxy.ClientProxy";
	String SERVER_PROXY_CLASS = "com.infinityraider.pokernight.proxy.ServerProxy";
	String GUI_FACTORY_CLASS = "com.infinityraider.pokernight.gui.GuiFactory";

	String UPDATE_URL = /*^${mod.update_url}^*/ "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
}
