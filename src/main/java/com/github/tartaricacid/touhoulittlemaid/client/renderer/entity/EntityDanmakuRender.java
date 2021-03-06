package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.danmaku.DanmakuColor;
import com.github.tartaricacid.touhoulittlemaid.danmaku.DanmakuType;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityDanmaku;
import com.github.tartaricacid.touhoulittlemaid.util.RenderHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly(Side.CLIENT)
public class EntityDanmakuRender extends Render<EntityDanmaku> {
    public static final Factory FACTORY = new EntityDanmakuRender.Factory();
    private static final ResourceLocation SINGLE_PLANE_DANMAKU = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/entity/singe_plane_danmaku.png");
    private static final ResourceLocation AMULET_DANMAKU = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/entity/amulet_danmaku.png");
    private static final ResourceLocation STAR_DANMAKU = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/entity/star_danmaku.png");
    private static final ResourceLocation PETAL_DANMAKU = new ResourceLocation(TouhouLittleMaid.MOD_ID, "entity/petal.obj");
    private static final ResourceLocation KNIFE_TOP_DANMAKU = new ResourceLocation(TouhouLittleMaid.MOD_ID, "entity/knife_top.obj");
    private static final ResourceLocation KNIFE_BOTTOM_DANMAKU = new ResourceLocation(TouhouLittleMaid.MOD_ID, "entity/knife_bottom.obj");
    private static List<BakedQuad> PETAL_QUADS;
    private static List<BakedQuad> KNIFE_TOP_QUADS;
    private static List<BakedQuad> KNIFE_BOTTOM_QUADS;

    private EntityDanmakuRender(RenderManager renderManagerIn) {
        super(renderManagerIn);
        PETAL_QUADS = getObjQuads(PETAL_DANMAKU);
        KNIFE_TOP_QUADS = getObjQuads(KNIFE_TOP_DANMAKU);
        KNIFE_BOTTOM_QUADS = getObjQuads(KNIFE_BOTTOM_DANMAKU);
    }

    private static List<BakedQuad> getObjQuads(ResourceLocation obj) {
        try {
            IModel model = ModelLoaderRegistry.getModel(obj);
            IBakedModel bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
            return bakedModel.getQuads(null, null, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    @Override
    public void doRender(@Nonnull EntityDanmaku entity, double x, double y, double z, float entityYaw, float partialTicks) {
        DanmakuType type = entity.getType();
        switch (type) {
            case PELLET:
            case BALL:
            case ORBS:
            case BIG_BALL:
            case BUBBLE:
            case HEART:
                renderSinglePlaneDanmaku(entity, x, y, z, type);
                break;
            case JELLYBEAN:
                renderJellyBeanDanmaku(entity, x, y, z);
                break;
            case AMULET:
                renderAmuletDanmaku(entity, x, y, z);
                break;
            case STAR:
            case BIG_STAR:
                renderStarDanmaku(entity, x, y, z, partialTicks);
                break;
            case PETAL:
                renderPetalDanmaku(entity, x, y, z);
                break;
            case KNIFE:
                renderKnifeDanmaku(entity, x, y, z);
                break;
            default:
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityDanmaku entity) {
        return SINGLE_PLANE_DANMAKU;
    }

    private void renderSinglePlaneDanmaku(EntityDanmaku entity, double x, double y, double z, DanmakuType type) {
        // 获取相关数据
        DanmakuColor color = entity.getColor();
        // 材质宽度
        int w = 416;
        // 材质长度
        int l = 192;

        // 依据类型颜色开始定位材质位置（材质块都是 32 * 32 大小）
        double pStartU = 32 * color.ordinal();
        double pStartV = 32 * type.ordinal();

        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX,
                1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufBuilder = tessellator.getBuffer();

        bufBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        this.renderManager.renderEngine.bindTexture(SINGLE_PLANE_DANMAKU);

        bufBuilder.pos(-type.getSize(), type.getSize(), 0).tex((pStartU + 0) / w, (pStartV + 0) / l).endVertex();
        bufBuilder.pos(-type.getSize(), -type.getSize(), 0).tex((pStartU + 0) / w, (pStartV + 32) / l).endVertex();
        bufBuilder.pos(type.getSize(), -type.getSize(), 0).tex((pStartU + 32) / w, (pStartV + 32) / l).endVertex();
        bufBuilder.pos(type.getSize(), type.getSize(), 0).tex((pStartU + 32) / w, (pStartV + 0) / l).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private void renderJellyBeanDanmaku(EntityDanmaku entity, double x, double y, double z) {
        DanmakuColor color = entity.getColor();
        float yaw = entity.rotationYaw + 90;
        float pitch = -entity.rotationPitch;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        {
            GlStateManager.translate(x, y + 0.3, z);
            GlStateManager.rotate(yaw, 0, 1, 0);
            GlStateManager.rotate(pitch, 0, 0, 1);
            GlStateManager.scale(0.5f, 0.25f, 0.25f);
            GlStateManager.color(255, 255, 255);
            GlStateManager.callList(RenderHelper.SPHERE_OUT_INDEX);
            GlStateManager.callList(RenderHelper.SPHERE_IN_INDEX);
        }
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y + 0.3, z);
            GlStateManager.rotate(yaw, 0, 1, 0);
            GlStateManager.rotate(pitch, 0, 0, 1);
            GlStateManager.scale(0.6f, 0.3f, 0.3f);
            GlStateManager.color(color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue());
            GlStateManager.callList(RenderHelper.SPHERE_OUT_INDEX);
            GlStateManager.callList(RenderHelper.SPHERE_IN_INDEX);
        }
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderAmuletDanmaku(EntityDanmaku entity, double x, double y, double z) {
        DanmakuType type = DanmakuType.AMULET;
        DanmakuColor color = entity.getColor();
        float yaw = entity.rotationYaw;
        float pitch = 90 - entity.rotationPitch;
        // 材质宽度
        int w = 416;
        double pStartU = 32 * color.ordinal();

        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        GlStateManager.translate(x, y + 0.12, z);
        GlStateManager.rotate(yaw, 0, 1, 0);
        GlStateManager.rotate(pitch, 1, 0, 0);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufBuilder = tessellator.getBuffer();
        this.renderManager.renderEngine.bindTexture(AMULET_DANMAKU);

        bufBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufBuilder.pos(-type.getSize(), type.getSize(), 0).tex((pStartU + 0) / w, 0).endVertex();
        bufBuilder.pos(-type.getSize(), -type.getSize(), 0).tex((pStartU + 0) / w, 1).endVertex();
        bufBuilder.pos(type.getSize(), -type.getSize(), 0).tex((pStartU + 32) / w, 1).endVertex();
        bufBuilder.pos(type.getSize(), type.getSize(), 0).tex((pStartU + 32) / w, 0).endVertex();
        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private void renderStarDanmaku(EntityDanmaku entity, double x, double y, double z, float partialTicks) {
        // 获取相关数据
        DanmakuColor color = entity.getColor();
        DanmakuType type = entity.getType();
        // 材质宽度
        int w = 416;
        // 材质长度
        int l = 64;

        // 依据类型颜色开始定位材质位置（材质块都是 32 * 32 大小）
        double pStartU = 32 * color.ordinal();
        double pStartV = 32 * (type == DanmakuType.BIG_STAR ? 0 : 1);

        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX,
                1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entity.ticksExisted + partialTicks) * 5, 0, 0, 1);
        GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufBuilder = tessellator.getBuffer();

        bufBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        this.renderManager.renderEngine.bindTexture(STAR_DANMAKU);

        bufBuilder.pos(-type.getSize(), type.getSize(), 0).tex((pStartU + 0) / w, (pStartV + 0) / l).endVertex();
        bufBuilder.pos(-type.getSize(), -type.getSize(), 0).tex((pStartU + 0) / w, (pStartV + 32) / l).endVertex();
        bufBuilder.pos(type.getSize(), -type.getSize(), 0).tex((pStartU + 32) / w, (pStartV + 32) / l).endVertex();
        bufBuilder.pos(type.getSize(), type.getSize(), 0).tex((pStartU + 32) / w, (pStartV + 0) / l).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private void renderPetalDanmaku(EntityDanmaku entity, double x, double y, double z) {
        DanmakuColor color = entity.getColor();
        float yaw = entity.rotationYaw - 90;
        float pitch = entity.rotationPitch;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buff = tessellator.getBuffer();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(yaw, 0, 1, 0);
            GlStateManager.rotate(pitch, 0, 0, 1);
            GlStateManager.scale(0.1f, 0.1f, 0.1f);
            buff.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
            for (BakedQuad quad : PETAL_QUADS) {
                LightUtil.renderQuadColor(buff, quad, 0xffffffff);
            }
            tessellator.draw();
            GlStateManager.popMatrix();
        }
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y - 0.02, z);
            GlStateManager.rotate(yaw, 0, 1, 0);
            GlStateManager.rotate(pitch, 0, 0, 1);
            GlStateManager.scale(0.12f, 0.12f, 0.12f);
            buff.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
            for (BakedQuad quad : PETAL_QUADS) {
                LightUtil.renderQuadColor(buff, quad, 0xaa_00_00_00 + color.getRgb());
            }
            tessellator.draw();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderKnifeDanmaku(EntityDanmaku entity, double x, double y, double z) {
        DanmakuColor color = entity.getColor();
        float yaw = entity.rotationYaw + 180;
        float pitch = entity.rotationPitch;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        GlStateManager.translate(x, y + 0.1, z);
        GlStateManager.rotate(yaw, 0, 1, 0);
        GlStateManager.rotate(pitch, 1, 0, 0);
        GlStateManager.scale(0.04f, 0.04f, 0.04f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buff = tessellator.getBuffer();
        {
            buff.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
            for (BakedQuad quad : KNIFE_TOP_QUADS) {
                LightUtil.renderQuadColor(buff, quad, 0xeeffffff);
            }
            tessellator.draw();
        }
        {
            buff.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
            for (BakedQuad quad : KNIFE_BOTTOM_QUADS) {
                LightUtil.renderQuadColor(buff, quad, 0xfe_00_00_00 + color.getRgb());
            }
            tessellator.draw();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }


    public static class Factory implements IRenderFactory<EntityDanmaku> {
        @Override
        public Render<? super EntityDanmaku> createRenderFor(RenderManager manager) {
            return new EntityDanmakuRender(manager);
        }
    }
}
