from PIL import Image
from os import path

path_to_folder = "D:\\Perso\\Code\\HO\\src\\main\\resources\\gui\\bilder\\match_events\\"

combinations = {"converted_penalty.png": ("goal.png", "penalty.png", "v"),
                "me_206.png": ("miss.png", "speciality-4.png", "v")}

def get_concat_h(im1, im2, out):
    dst = Image.new("RGBA", (im1.width + im2.width, im1.height), (255, 0, 0, 0))
    dst.paste(im1, (0, 0))
    dst.paste(im2, (im1.width, 0))
    dst.save(path_to_folder+out, "PNG")

def get_concat_v(im1, im2, out):
    dst = Image.new("RGBA", (im1.width, im1.height + im2.height), (255, 0, 0, 0))
    dst.paste(im1, (0, 0))
    dst.paste(im2, (0, im1.height))
    dst.save(path_to_folder+out, "PNG")


for k, v in combinations.items():
    name_img1, name_img2, operation = v

    if not path.exists("path_to_folder+out"):
        try:
            im1 = Image.open(path_to_folder + name_img1)
            im2 = Image.open(path_to_folder + name_img2)
            if operation == "v":
                get_concat_v(im1, im2, k)
            else:
                get_concat_h(im1, im2, k)
            print(f"{k} has been generated")
        except Exception as e:
            print("operation failed")
            print(k)
            print(v)
            print(e)
