/**
 */
package PuzzleADL.provider;


import PuzzleADL.LanguageModule;
import PuzzleADL.PuzzleADLFactory;
import PuzzleADL.PuzzleADLPackage;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link PuzzleADL.LanguageModule} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class LanguageModuleItemProvider extends NamedElementItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LanguageModuleItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

		}
		return itemPropertyDescriptors;
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(PuzzleADLPackage.Literals.LANGUAGE_MODULE__REQUIRED_INTERFACE);
			childrenFeatures.add(PuzzleADLPackage.Literals.LANGUAGE_MODULE__PROVIDED_INTERFACE);
			childrenFeatures.add(PuzzleADLPackage.Literals.LANGUAGE_MODULE__ABSTRACT_SYNTAX);
			childrenFeatures.add(PuzzleADLPackage.Literals.LANGUAGE_MODULE__SEMANTICS_IMPLEMENTATION);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns LanguageModule.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/LanguageModule"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((LanguageModule)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_LanguageModule_type") :
			getString("_UI_LanguageModule_type") + " " + label;
	}
	

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(LanguageModule.class)) {
			case PuzzleADLPackage.LANGUAGE_MODULE__REQUIRED_INTERFACE:
			case PuzzleADLPackage.LANGUAGE_MODULE__PROVIDED_INTERFACE:
			case PuzzleADLPackage.LANGUAGE_MODULE__ABSTRACT_SYNTAX:
			case PuzzleADLPackage.LANGUAGE_MODULE__SEMANTICS_IMPLEMENTATION:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(PuzzleADLPackage.Literals.LANGUAGE_MODULE__REQUIRED_INTERFACE,
				 PuzzleADLFactory.eINSTANCE.createRequiredInterface()));

		newChildDescriptors.add
			(createChildParameter
				(PuzzleADLPackage.Literals.LANGUAGE_MODULE__PROVIDED_INTERFACE,
				 PuzzleADLFactory.eINSTANCE.createProvidedInterface()));

		newChildDescriptors.add
			(createChildParameter
				(PuzzleADLPackage.Literals.LANGUAGE_MODULE__ABSTRACT_SYNTAX,
				 PuzzleADLFactory.eINSTANCE.createAbstractSyntaxImplementation()));

		newChildDescriptors.add
			(createChildParameter
				(PuzzleADLPackage.Literals.LANGUAGE_MODULE__SEMANTICS_IMPLEMENTATION,
				 PuzzleADLFactory.eINSTANCE.createSemanticsImplementation()));
	}

}
